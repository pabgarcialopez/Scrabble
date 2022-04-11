package logic;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import command.Command;
import containers.Board;
import containers.GamePlayers;
import containers.GameTiles;
import exceptions.CommandExecuteException;
import simulatedObjects.Box;
import simulatedObjects.Player;
import simulatedObjects.Tile;
import storage.GameLoader;
import utils.StringUtils;
import view.ScrabbleObserver;

/* APUNTES GENERALES

La clase Game encapsula toda la lógica principal de juego.

Para ello, se necesita:

- Saber si el juego ha sido inicializado, y si ya se ha colocado alguna palabra en el tablero.
- Contenedores de tipo Board, GamePlayers, GameTiles.
- El turno del jugador actual y el número consecutivo de turnos saltados.
- Conocer si se han puesto palabras en el tablero, si el juego ha terminado, y la causa de ello.
- Listas de las palabras usadas, y todas las palabras válidas.
- Un comprobador de palabras.
- Un objeto Random para establecer aleatoriedad.
- Lista de observadores para actualizar las vistas en función del modelo.

*/

public class Game {
	
	private static final int EXTRA_POINTS = 50;
	private static final int PASSED_TURNS_TO_END_GAME = 2;
	
	// Para poder jugar partidas por fichero sin tener 
	// que incluir saltos de línea en el fichero de entrada.
	private static final boolean _pausePermitted = false;
	
	private static boolean _gameInitiated;
	private static boolean _wordsInBoard;
	
	public static int _seed;

	private Board board;
	private GamePlayers players;
	private GameTiles tiles;
	
	private int currentTurn;
	private int numConsecutivePassedTurns;
	
	private boolean gameFinished;
	private String gameFinishedCause;
	
	private List<String> usedWords;
	private static List<String> words;
	
	private WordChecker wordChecker;
	
	private Random random;
	
	private List<ScrabbleObserver> observers;

	public Game() {
		
		_gameInitiated = false;
		_wordsInBoard = false;
		
		this.board = null;
		this.players = new GamePlayers((List<Player>) new ArrayList<Player>());
		this.tiles = null;
		
		this.currentTurn = 0;
		this.numConsecutivePassedTurns = 0;
		
		this.gameFinished = false;
		this.gameFinishedCause = "";
		
		this.usedWords = null;
		
		this.wordChecker = new WordChecker(this);
		this.observers = new ArrayList<ScrabbleObserver>();
	}
	
	/* Método reset:
	 * 
	 * Este método recibe desde la clase GameLoader ciertos valores con los que inicializar atributos de esta clase.
	 * 
	 * Si el fichero que se haya cargado tenía jugadores predeterminados, se llama al método addPlayers de esta clase
	 * (ver su funcionalidad).
	 * 
	 * En caso contrario, hay que pedirlos al usuario (ya sea por consola o por interfaz gráfica).
	 * 
	 * IMPORTANTE: la inicialización del atributo de la clase Random y el atributo que representa el turno actual,
	 * deben inicializarse antes de ejecutar el gestionar el añadido de jugadores (if-else), por razones de diseño.
	 */
	public void reset(int currentTurn, int numConsecutivePassedTurns, boolean wordsInBoard,
			boolean gameFinished, GamePlayers players, GameTiles tiles, Board board, List<String> usedWords) {
		
		this.board = board;
		this.tiles = tiles;
		
		this.random = new Random(Game.getSeed());

		this.currentTurn = currentTurn;
		this.numConsecutivePassedTurns = numConsecutivePassedTurns;
		
		if(players.getNumPlayers() != 0) {
			addPlayers(players);
		}
		
		else {
			for(ScrabbleObserver o : this.observers)
				o.onPlayersNotAdded(this);
		}
		
		_wordsInBoard = wordsInBoard;
		this.gameFinished = gameFinished;
		
		this.usedWords = usedWords;
		
		_gameInitiated = true;
		
		for(int i = 0; i < this.observers.size(); ++i)
			this.observers.get(i).onReset(this);
	}
	
	/* Método writeAWord:
	 * 
	 * Lleva a cabo la acción de escribir una palabra en el tablero.
	 * 
	 * Primero convierte la palabra a un formato deseado (en minúsculas y sin acentos), y delega
	 * en la clase WordChecker la comprobación de la validez de los parámetros recibidos (cabiendo
	 * la posibilidad de que se lancen excepciones).
	 * 
	 * Si no se ha detectado ningún error en los parámetros, se añade la palabra introducida a la lista
	 * de palabras usadas. También se comprueba si el jugador ha usado todas sus fichas, en cuyo caso,
	 * se le añaden EXTRA_POINTS a los puntos que hubiese acumulado con la palabra colocada.
	 * 
	 * A continuación se delega en GamePlayers la entraga de puntos al jugador; se establece que el tablero
	 * no está vacío, y que el número de turnos consecutivos saltados es cero.
	 * 
	 * Por último, se notifica a los observadores que se ha escrito una palabra; se delega en GamePlayers
	 * el robo de fichas por parte del jugador, y se avanza el turno.
	 */
	public boolean writeAWord(String word, int posX, int posY, String direction) {
		
		try {
			word = StringUtils.removeAccents(word);
			word = word.toLowerCase();
			this.wordChecker.checkArguments(word, posX, posY, direction);
		}
		catch(CommandExecuteException cee) {
			if(humanIsPlaying()) {
				for(ScrabbleObserver o : this.observers)
					o.onError(cee.getMessage());
			}
			return false;
		}
		
		addUsedWord(word);
		
		int numPlayerTilesBefore = this.players.getNumPlayerTiles(this.currentTurn);
		
		assignTiles(word, posX, posY, direction);
		int points = calculatePoints(word, posX, posY, direction);
		
		int extraPoints = 0;
		if(numPlayerTilesBefore == 7 && this.players.getNumPlayerTiles(this.currentTurn) == 0)
			extraPoints = EXTRA_POINTS;
		
		players.givePoints(this.currentTurn, points + extraPoints);
		
		_wordsInBoard = true;
		numConsecutivePassedTurns = 0;
		
		for(ScrabbleObserver o : this.observers)
			o.onWordWritten(this, word, posX, posY, direction, points, extraPoints);
		
		players.drawTiles(this, currentTurn);
	
		nextTurn();
		
		return true;
	}
	
	/* Método decideFirstTurn:
	 * 
	 * Establece, si es necesario, el orden de juego de una partida.
	 * 
	 * Primero comprueba si el turno ya se ha inicializado (se encuentra en el
	 * intervalo [0, players.getNumPlayers()]; en caso de no haberse inicializado
	 * tendría el valor -1).
	 * 
	 * Se construye un array de letras aleatorias obtenidas por los jugadores,
	 * y se comparan en orden lexicográfico para decidir quién empieza la partida
	 * (gana quién más se acerque a la A, y en caso de empate, se prioriza el orden 
	 * en el que se cogió la letra).
	 * 
	 * Por último, se notifica a los observadores.
	 */
	public void decideFirstTurn() {
		
		if(0 <= this.currentTurn && this.currentTurn < this.players.getNumPlayers())
			return;
		
		this.currentTurn = 0;
		
		String[] lettersObtained = new String[this.getNumPlayers()];
		
		for(int i = 0; i < this.getNumPlayers(); ++i) 
			lettersObtained[i] = this.randomTile().getLetter();
	
		// La partida es empezada por quien obtenga la letra mas cercana a la A.
		for(int i = 1; i < this.getNumPlayers(); ++i)
			if (lettersObtained[i].compareTo(lettersObtained[this.currentTurn]) < 0) 
				this.currentTurn = i;
		
		for(ScrabbleObserver o : this.observers)
			o.onFirstTurnDecided(this, lettersObtained);
	}
	
	/* Método passTurn:
	 * 
	 * Pasa de turno en el juego (método nextTurn), incrementando previamente el número de turnos
	 * consecutivos saltados, y notificando a los observadores.
	 */
	public void passTurn() {
		++this.numConsecutivePassedTurns;
		
		for(ScrabbleObserver o : this.observers)
			o.onPassed(this);
		
		nextTurn();
	}
	
	/* Método nextTurn:
	 * Avanza el turno actual en uno.
	 */
	private void nextTurn() {
		this.currentTurn = (this.currentTurn + 1) % this.getNumPlayers();
	}

	/* Método automaticPlay:
	 * Delega en GamePlayers el juego de jugadores automáticos.
	 */
	public void automaticPlay() {
		this.players.automaticPlay(this.currentTurn, this);
	}

	/* Método swapTile:
	 * 
	 * Primero comprueba que queden fichas para poder realizar un intercambio.
	 * 
	 * En caso de que así sea, se obtiene una ficha aleatoria del jugador,
	 * que es introducida en el saco, y eliminada de su mano; después el jugador
	 * obtiene su nueva ficha (acciones delegadas en los containers respectivos).
	 * 
	 * Se incrementan los turnos saltados, se notifica a los observadores y se
	 * avanza al siguiente turno (método nextTurn).
	 */
	public boolean swapTile() {
		
		if(tiles.getSize() <= 0) {
			if(humanIsPlaying()) {
				for(ScrabbleObserver o : this.observers)
					o.onError("No hay fichas para robar.");
			}
			
			return false;
		}
		
		int randomNumberForTile = (int) (getRandomDouble() * players.getNumPlayerTiles(currentTurn));
		
		Tile randomTile = players.getPlayerTile(currentTurn, randomNumberForTile);
		
		// Aniadimos la ficha al saco original
		tiles.add(randomTile);
		
		// Quitamos la ficha al jugador
		players.removePlayerTile(currentTurn, randomTile);
		
		// Le damos una ficha al jugador aleatoria
		players.drawTiles(this, currentTurn);
		
		++this.numConsecutivePassedTurns;
		
		for(ScrabbleObserver o : this.observers)
			o.onSwapped(this);
		
		nextTurn();
		
		return true;
	}
	
	/* Método removeTile:
	 * Delega en la clase GameTiles la acción de eliminar una ficha del saco.
	 */
	public void removeTile(Tile tile) {
		tiles.remove(tile);
	}
	
	/* Método randomTile:
	 * Devuelve una ficha aleatoria del saco (si la hay).
	 */
	public Tile randomTile() {
		if (tiles.getNumTiles() == 0)
			return null;
		return tiles.getTile((int) (this.getRandomDouble() * this.tiles.getNumTiles()));
	}
	
	/* Método calculatePoints:
	 * 
	 * Para cada letra de la palabra recibida por parámetrp, se delega en la clase Board la acción de 
	 * obtener el total de puntos asociado a esa letra (points) en esa casilla (wordMultiplier).
	 * 
	 * Durante el bucle, se obtiene el total de puntos asociados a las letras, y el total asociado a los
	 * multiplicadores de palabras (modificadores DOUBLE_WORD y TRIPLE_WORD).
	 * 
	 * Finalmente se devuelve el resultado de multiplicar los puntos de letras con los puntos de los modificadores.
	 */
	private int calculatePoints(String word, int posX, int posY, String direction) {
		
		int vertical = ("V".equalsIgnoreCase(direction) ? 1 : 0);
		int horizontal = ("H".equalsIgnoreCase(direction) ? 1 : 0);
		
		int points = 0, wordMultiplier = 1;
		
		for (int i = 0; i < word.length(); ++i) {
			points += this.board.getPoints(posX + i * vertical, posY + i * horizontal);
			wordMultiplier *= this.board.getWordMultiplier(posX + i * vertical, posY + i * horizontal);
		}
		
		points *= wordMultiplier;
		
		return points;
	}

	/* Método assignTiles:
	 * 
	 * Por cada letra de la palabra recibida por parámetro, se obtiene la ficha asociada a esa letra (acción delegada
	 * en la clase GamePlayers), se asigna dicha dicha en el tablero (acción delegada en la clase Board), y se elimina
	 * dicha ficha de la mano del jugador (acción delegada en la clase GamePlayers).
	 */
	public void assignTiles(String word, int posX, int posY, String direction) {
		
		int vertical = ("V".equalsIgnoreCase(direction) ? 1 : 0);
		int horizontal = ("H".equalsIgnoreCase(direction) ? 1 : 0);
		
		for (int i = 0; i < word.length(); ++i) {
			
			Tile tile = this.players.getPlayerTile(this.currentTurn, String.valueOf(word.charAt(i)));
			
			try {
				this.board.assignTile(tile, posX + i * vertical, posY + i * horizontal);
				this.players.removePlayerTile(this.currentTurn, tile);
			}
			
			catch (IllegalArgumentException iae) {};
		}
	}

	/* Método update:
	 * 
	 * Comprueba si el juego ha terminado por alguna de las siguientes razones:
	 * - No quedan fichas en el saco y el jugador anterior al actual no tiene tampoco.
	 * - Todos los jugadores han pasado su turno PASSED_TURNS_TO_END_GAME veces.
	 * 
	 * En caso de que alguna se dé, se registra la causa del final de la partida.
	 * 
	 * Después notifica a los observadores para que actualicen su estado, 
	 * y si el juego se ha acabado, también se les notifica de ello.
	 */
	public void update() {
		
		// Si no quedan fichas en el saco, y el jugador actual no tiene fichas
		if(this.getRemainingTiles() == 0 && 
				this.players.getNumPlayerTiles((this.currentTurn + this.getNumPlayers() - 1) % this.getNumPlayers()) == 0) {
			this.gameFinished = true;
			
			this.gameFinishedCause = "La partida ha finalizado: no quedan fichas para robar y el jugador " 
			+ this.players.getPlayerName(this.currentTurn + this.getNumPlayers() - 1)
			+ " se ha quedado sin fichas."+ StringUtils.DOUBLE_LINE_SEPARATOR;
		}
		
		if(this.numConsecutivePassedTurns == this.getNumPlayers() * PASSED_TURNS_TO_END_GAME) {
			this.gameFinished = true;
			this.gameFinishedCause = "La partida ha finalizado: todos los jugadores han pasado " 
					+ PASSED_TURNS_TO_END_GAME + " turnos." + StringUtils.LINE_SEPARATOR;
		}
		
		for(ScrabbleObserver o : this.observers)
			o.onUpdate(this);
		
		if(gameIsFinished()) {
			for(ScrabbleObserver o : this.observers)
				o.onEnd(gameFinishedCause + getWinnerName());
		}
	}
	
	/* Método getWinnerGame:
	 * 
	 * Devuelve un mensaje detallando quién o quiénes han ganado la partida.
	 * Delega parte de la acción en la clase GamePlayers (método getWinners).
	 */
	private String getWinnerName() {
		List<Integer> winners = this.players.getWinners();
		String winnersMessage = null;
		
		if(winners.size() == 1) {
			winnersMessage = "¡" + this.players.getPlayerName(winners.get(0)) + " gana la partida ";
		}
		else {
			winnersMessage = "Ha habido un empate... ¡";
			for(int i = 0; i < winners.size(); ++i) {
				winnersMessage += this.players.getPlayerName(winners.get(i));
				if(i < winners.size() - 2)
					winnersMessage += ", ";
				else if (i == winners.size() - 2)
					winnersMessage += " y ";
			}
			
			winnersMessage += " ganan la partida ";
		}
		
		winnersMessage += String.format("con %s puntos!", this.players.getPlayerPoints(winners.get(0)));
		
		return winnersMessage;
	}

	/* Método getStatus:
	 * Devuelve un String con el estado actual del juego y del jugador (delegación en GamePlayers).
	 */
	public String getStatus() {
		String status = "Semilla: " + _seed + StringUtils.LINE_SEPARATOR;
		status += "Fichas restantes: " + this.getRemainingTiles() + StringUtils.LINE_SEPARATOR;
		status += players.getPlayerStatus(currentTurn) + StringUtils.LINE_SEPARATOR;

		if (!humanIsPlaying())
			status += "Cargando... Por favor, espera." + StringUtils.LINE_SEPARATOR;

		return status;
	}
	
	/* Método addUsedWord
	 * Añade la palabra recibida por parámetro a la lista de palabras usadas.
	 * La lista se mantiene siempre ordenada para poder realizar una búsqueda eficiente.
	 */
	public void addUsedWord(String word) {
		this.usedWords.add(word.toLowerCase());
		Collections.sort(this.usedWords);
	}
	
	/* Método initWordList:
	 * Este método estático es llamado desde la clase Main, y delega estáticamente
	 * en la clase GameLoader la carga de las palabras válidas de la partida.
	 */
	public static void initWordList() throws JSONException, FileNotFoundException {
		words = GameLoader.loadWordList();
	}
	
	/* Método addPlayers:]
	 * Este método inicializa el container GamePlayers al recibido por parámetro.
	 * Además, completa las fichas que a los jugadores les pueda faltar (método initPlayerTiles) (caso de nueva partida).
	 * Por último, si es necesario (nueva partida), se establece el orden de juego (método decideFirstTurn).
	 */
	public void addPlayers(GamePlayers players) {

		this.players = players;
		initPlayerTiles();
		decideFirstTurn();	
	}
	
	/* Método initPlayerTiles:
	 * Completa las fichas que a los jugadores les pueda faltar.
	 */
	public void initPlayerTiles() {
		for(int i = 0; i < this.players.getNumPlayers(); i++) {
			players.drawTiles(this, i);
		}
	}

	/* Método addObserver:
	 * Añade a un observador no nulo y todavía no añadido recibido por parámetro 
	 * a la lista de tipo ScrabbleObserver. También se notifica a dicho observer de esta acción.
	 */
	public void addObserver(ScrabbleObserver o) {
		if(o != null && !this.observers.contains(o)) {
			this.observers.add(o);
			o.onRegister(this);
		}
	}
	
	/* Método removeObserver:
	 * Elimina a un observador no nulo de la lista de tipo ScrabbleObserver.
	 */
	public void removeObserver(ScrabbleObserver o) {
		if(o != null)
			this.observers.remove(o);
	}
	
	/* Método userExits:
	 * Actualiza el estado del juego a finalizado, y establece la causa de ello.
	 * Notifica a los observadores de la terminación de la partida.
	 */
	public void userExits() {
		
		this.gameFinished = true;
		
		this.gameFinishedCause = "El jugador " 
				+ this.players.getPlayerName(this.currentTurn) + " ha finalizado la partida." 
				+ StringUtils.DOUBLE_LINE_SEPARATOR;
		
		for(ScrabbleObserver o : this.observers)
			o.onEnd(gameFinishedCause + getWinnerName());
	}
	
	/* Método resetPlayers:
	 * Delega en la clase GamePlayers el reseteo de los jugadores.
	 */
	public void resetPlayers() {
		this.players.reset();
	}
	
	/* Método printHelpMessage:
	 * Notifica a ConsoleView la impresión del mensaje de ayuda.
	 * 
	 * NOTA DE DISEÑO: el método printHelpMessage de la interfaz ScrabbleObserver
	 * es default porque solo es coherente que ConsoleView lo implemente.
	 * Sin embargo, para no romper la abstracción, se sigue recorriendo el
	 * array de ScrrableObservers para llevar a cabo la acción.
	 */
	public void printHelpMessage(Command[] commands) {
		for(ScrabbleObserver o : observers) {
			o.printHelpMessage(commands);
		}
	}
	
	// Getters
	
	public static boolean getGameInitiated() {
		return _gameInitiated;
	}
	
	public static boolean getWordsInBoard() {
		return _wordsInBoard;
	}
	
	public static boolean isPausePermitted() {
		return _pausePermitted;
	}
	
	public static int getSeed() {
		return _seed;
	}
	
	public int getBoardSize() {
		return board.getBoardSize();
	}
	
	public Box getBoxAt(int i, int j) {
		return board.getBoxAt(i, j);
	}
	
	public Board getBoard() {
		return this.board;
	}
	
	private int getNumPlayers() {
		return this.players.getNumPlayers();
	}
	
	public GamePlayers getPlayers() {
		return this.players;
	}
	
	public boolean humanIsPlaying() {
		return players.humanIsPlaying(currentTurn);
	}

	public int getRemainingTiles() {
		return this.tiles.getNumTiles();
	}
	
	public int getCurrentTurn() {
		return this.currentTurn;
	}
	
	public boolean gameIsFinished() {
		return this.gameFinished;
	}

	public List<String> getWordsList() {
		return Collections.unmodifiableList(words);
	}

	public List<String> getUsedWords() {
		return Collections.unmodifiableList(usedWords);
	}

	private Double getRandomDouble() {
		return this.random.nextDouble();
	}
	
	public static void setSeed(int seed) {
		_seed = seed;
	}

	public JSONObject report() {

		JSONObject jo = new JSONObject();

		jo.put("current_turn", this.currentTurn);
		jo.put("consecutive_turns_passed", this.numConsecutivePassedTurns);
		jo.put("words_in_board", _wordsInBoard);
		jo.put("game_finished", this.gameFinished);

		JSONArray words = new JSONArray();
		for (int i = 0; i < this.usedWords.size(); ++i)
			words.put(this.usedWords.get(i));

		JSONObject usedWords = new JSONObject();
		usedWords.put("words", words);

		jo.put("used_words", usedWords);
		jo.put("game_players", this.players.report());
		jo.put("game_tiles", this.tiles.report());
		jo.put("game_board", this.board.report());
		
		jo.put("seed", _seed);

		return jo;
	}

	

	
}
