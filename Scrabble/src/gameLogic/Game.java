package gameLogic;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import exceptions.CommandExecuteException;
import gameContainers.Board;
import gameContainers.GamePlayers;
import gameContainers.GameTiles;
import gameObjects.Box;
import gameObjects.Player;
import gameObjects.Tile;
import gameUtils.StringUtils;
import gameView.ScrabbleObserver;
import storage.GameLoader;

/* APUNTES GENERALES

La clase Game encapsula toda la lógica de juego.

Para ello, necesita:


- Contenedores de tipo Board, GamePlayers, GameTiles.
- El turno del jugador actual y el número consecutivo de turnos saltados.
- Conocer si se han puesto palabras en el tablero y si el juego ha terminado.
- Listas de las palabras usadas, y todas las palabras válidas.
- Un comprobador de palabras.
- Un objeto Random para establecer aleatoriedad.
- Lista de observadores para actualizar las vistas en función del modelo.

*/

public class Game {
	
	private static final int EXTRA_POINTS = 50;
	private static final int PASSED_TURNS_TO_END_GAME = 2;
	private static boolean gameInitiated;
	
	private Board board;
	private GamePlayers players;
	private GameTiles tiles;
	
	private int currentTurn;
	private int numConsecutivePassedTurns;
	
	private boolean wordsInBoard;
	private boolean gameFinished;
	private String gameFinishedCause;
	
	private List<String> usedWords;
	private static List<String> words;
	
	private WordChecker wordChecker;
	
	private Random random;
	
	private List<ScrabbleObserver> observers;

	public Game() {
		
		gameInitiated = false;
		
		this.board = null;
		this.players = new GamePlayers((List<Player>) new ArrayList<Player>());
		this.tiles = null;
		
		this.currentTurn = 0;
		this.numConsecutivePassedTurns = 0;
		
		this.wordsInBoard = false;
		this.gameFinished = false;
		this.gameFinishedCause = "";
		
		this.usedWords = null;
		
		this.wordChecker = new WordChecker(this);
		this.observers = new ArrayList<ScrabbleObserver>();

		
//		gameInitiated = false;
//		
//		this.observers = new ArrayList<ScrabbleObserver>();
//		this.wordChecker = new WordChecker(this);
//		this.players = new GamePlayers((List<Player>) new ArrayList<Player>());
	}
	
//	public Game(int currentTurn, int numConsecutivePassedTurns, boolean wordsInBoard,
//			boolean gameFinished, GamePlayers players, GameTiles tiles, Board board, List<String> usedWords) {
//		
//		this.observers = new ArrayList<ScrabbleObserver>();
//		this.wordChecker = new WordChecker(this);
//		
//		reset(currentTurn, numConsecutivePassedTurns, wordsInBoard, 
//				gameFinished, players, tiles, board, usedWords);
//	}
	
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
	 * deben inicializarse antes de ejecutar el if-else, por razones de diseño.
	 */
	public void reset(int currentTurn, int numConsecutivePassedTurns, boolean wordsInBoard,
			boolean gameFinished, GamePlayers players, GameTiles tiles, Board board, List<String> usedWords) {
		
		this.board = board;
		this.tiles = tiles;
		
		this.random = new Random();

		this.currentTurn = currentTurn;
		this.numConsecutivePassedTurns = numConsecutivePassedTurns;
		
		if(players.getNumPlayers() != 0) {
			addPlayers(players);
		}
		
		else {
			for(ScrabbleObserver o : this.observers)
				o.onPlayersNotAdded(this);
		}
		
		this.wordsInBoard = wordsInBoard;
		this.gameFinished = gameFinished;
		
		this.usedWords = usedWords;
		
		gameInitiated = true;
		
		for(int i = 0; i < this.observers.size(); ++i)
			this.observers.get(i).onReset(this);
		
		/* VERSION ANTERIOR
		
		this.numConsecutivePassedTurns = numConsecutivePassedTurns;
		this.wordsInBoard = wordsInBoard;
		this.gameFinished = gameFinished;
		
		this.tiles = tiles;
		this.board = board;
		this.usedWords = usedWords;
		this.random = new Random();
		this.currentTurn = currentTurn;
		
		if(players.getNumPlayers() != 0) {
			addPlayers(players);
		}
		
		else {
			for(ScrabbleObserver o : this.observers)
				o.onPlayersNotAdded(this);
		}
		
		gameInitiated = true;
		
		for(int i = 0; i < this.observers.size(); ++i)
			this.observers.get(i).onReset(this);
			
		 */
	}

	
	public void checkArguments(String word, int posX, int posY, String direction) throws CommandExecuteException {
		this.wordChecker.checkArguments(word, posX, posY, direction);
	}
	
	public boolean writeAWord(String word, int posX, int posY, String direction) {
		
		try {
			word = StringUtils.removeAccents(word.toLowerCase());
			this.wordChecker.checkArguments(word, posX, posY, direction);
		}
		catch(CommandExecuteException cee) {
			if(humanIsPlaying()) {
				for(ScrabbleObserver o : this.observers)
					o.onError(cee.getMessage());
			}
			return false;
		}
		
		addUsedWord(word.toLowerCase());
		
		int numPlayerTilesBefore = this.players.getNumPlayerTiles(this.currentTurn);
		assignTiles(word, posX, posY, direction);
		
		int points = calculatePoints(word, posX, posY, direction), extraPoints = 0;
		
		if(numPlayerTilesBefore == 7 && this.players.getNumPlayerTiles(this.currentTurn) == 0)
			extraPoints = EXTRA_POINTS;
		
		players.givePoints(this.currentTurn, points + extraPoints);
		
		this.wordsInBoard = true;
		numConsecutivePassedTurns = 0;
		
		for(ScrabbleObserver o : this.observers)
			o.onWordWritten(this, word, posX, posY, direction, points, extraPoints);
		
		players.drawTiles(this, currentTurn);
	
		nextTurn();
		
		return true;
	}
	
	/* Método decideFirstTurn:
	 * 
	 * Este método decide, si es necesario, el orden de juego de una partida.
	 * 
	 * Primero comprueba que 
	 *
	 */
	public void decideFirstTurn() {
		
		if(0 <= this.currentTurn && this.currentTurn < this.players.getNumPlayers())
			return;
		
		this.currentTurn = 0;
		
		String[] lettersObtained = new String[this.getNumPlayers()];
		
		for(int i = 0; i < this.getNumPlayers(); ++i) 
			lettersObtained[i] = this.getRandomTile().getLetter();
	
		// La partida es empezada por quien obtenga la letra mas cercana a la A.
		for(int i = 1; i < this.getNumPlayers(); ++i)
			if (lettersObtained[i].compareTo(lettersObtained[this.currentTurn]) < 0) 
				this.currentTurn = i;
		
		for(ScrabbleObserver o : this.observers)
			o.onFirstTurnDecided(this, lettersObtained);
	}
	
	public void passTurn() {
		++this.numConsecutivePassedTurns;
		
		for(ScrabbleObserver o : this.observers)
			o.onPassed(this);
		
		nextTurn();
	}
	
	private void nextTurn() {
		this.currentTurn = (this.currentTurn + 1) % this.getNumPlayers();
	}

	public void automaticPlay() {
		this.players.automaticPlay(this.currentTurn, this);
	}

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
	
	public void removeTile(Tile tile) {
		tiles.remove(tile);
	}
	
	private int calculatePoints(String word, int posX, int posY, String direction) {
		
		int points = 0;
		int wordMultiplier = 1;
		
		if ("V".equalsIgnoreCase(direction)) {
			for (int i = 0; i < word.length(); ++i) {
				points += this.board.getPoints(posX + i, posY);
				wordMultiplier *= this.board.getWordMultiplier(posX + i, posY);
			}
		}
		else {
			for (int i = 0; i < word.length(); ++i) {
				points += this.board.getPoints(posX, posY + i);
				wordMultiplier *= this.board.getWordMultiplier(posX, posY + i);
			}
		}
		
		points *= wordMultiplier;
		
		return points;
	}

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

	public void update() {
		
		// Si no quedan fichas en el saco, y el jugador actual no tiene fichas
		if(this.getRemainingTiles() == 0 && 
				this.players.getNumPlayerTiles((this.currentTurn + this.getNumPlayers() - 1) % this.getNumPlayers()) == 0) {
			this.gameFinished = true;
			
			this.gameFinishedCause = "La partida ha finalizado: no quedan fichas para robar." + StringUtils.DOUBLE_LINE_SEPARATOR;
		}
		
		if(this.numConsecutivePassedTurns == this.getNumPlayers() * PASSED_TURNS_TO_END_GAME) {
			this.gameFinished = true;
			this.gameFinishedCause = "La partida ha finalizado: todos los jugadores han pasado " 
					+ PASSED_TURNS_TO_END_GAME + " turnos." + StringUtils.DOUBLE_LINE_SEPARATOR;
		}
		
		for(ScrabbleObserver o : this.observers)
			o.onUpdate(this);
		
		if(gameIsFinished()) {
			for(ScrabbleObserver o : this.observers)
				o.onEnd(gameFinishedCause + getWinnerName());
		}
	}
	

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

	public void addUsedWord(String word) {
		this.usedWords.add(word.toLowerCase());
		Collections.sort(this.usedWords);
	}
	
	public static void initWordList() throws JSONException, FileNotFoundException {
		words = GameLoader.loadWordList();
	}
	
	/* Método addPlayers:
	 * 
	 * Este método inicializa el container GamePlayers al recibido por parámetro.
	 * Además, completa las fichas que a los jugadores les pueda faltar (caso de nueva partida).
	 * Por último, si es necesario (partida nueva), el método decideFirstTurn establecerá el orden
	 * de juego de la partida.
	 * 
	 */
	public void addPlayers(GamePlayers players) {

		this.players = players;
		this.initPlayerTiles();
		decideFirstTurn();	
	}
	
	/* Método initPlayerTiles:
	 * 
	 */
	public void initPlayerTiles() {
		for(int i = 0; i < this.players.getNumPlayers(); i++) {
			players.drawTiles(this, i);
		}
	}

	public void addObserver(ScrabbleObserver o) {
		if(o != null && !this.observers.contains(o)) {
			this.observers.add(o);
			o.onRegister(this);
		}
	}
	
	public void removeObserver(ScrabbleObserver o) {
		if(o != null && !this.observers.contains(o))
			this.observers.remove(o);
	}
	
	public void userExits() {
		
		this.gameFinished = true;
		
		this.gameFinishedCause = "El jugador " 
				+ this.players.getPlayerName(this.currentTurn) + " ha finalizado la partida." 
				+ StringUtils.DOUBLE_LINE_SEPARATOR;
		
		for(ScrabbleObserver o : this.observers)
			o.onEnd(gameFinishedCause + getWinnerName());
	}
	
	public void resetPlayers() {
		this.players.reset();
	}
	
	// GETTERS
	
	public boolean gameIsFinished() {
		return this.gameFinished;
	}
	
	public int getRemainingTiles() {
		return this.tiles.getNumTiles();
	}

	public int getBoardSize() {
		return board.getBoardSize();
	}

	public Box getBoxAt(int i, int j) {
		return board.getBoxAt(i,j);
	}
	
	private int getNumPlayers() {
		return this.players.getNumPlayers();
	}
	
	public Tile getRandomTile() {
		if(tiles.getNumTiles() == 0)
			return null;
		
		return tiles.getTile((int) (this.getRandomDouble() * this.tiles.getNumTiles()));
	}
	
	private Double getRandomDouble() {
		return this.random.nextDouble();
	}
	
	public String getStatus() {
		String status = "Fichas restantes: " + this.getRemainingTiles() + StringUtils.LINE_SEPARATOR;
		status += players.getPlayerStatus(currentTurn) + StringUtils.LINE_SEPARATOR;
		
		if(!humanIsPlaying())
			status += "Cargando... Por favor, espera." + StringUtils.LINE_SEPARATOR;
		
		return status;
	}
	
	public GamePlayers getPlayers() {
		return this.players;
	}
	
	public int getCurrentTurn() {
		return this.currentTurn;
	}

	public boolean getWordsInBoard() {
		return this.wordsInBoard;
	}

	public List<String> getWordsList() {
		return words;
	}

	public List<String> getUsedWords() {
		return this.usedWords;
	}

	public Board getBoard() {
		return this.board;
	}

	public boolean humanIsPlaying() {
		return players.humanIsPlaying(currentTurn);
	}
	
	public static boolean getGameInitiated() {
		return gameInitiated;
	}
	
	public JSONObject report() {
		
		JSONObject jo = new JSONObject();
		
		jo.put("current_turn", this.currentTurn);
		jo.put("consecutive_turns_passed", this.numConsecutivePassedTurns);
		jo.put("words_in_board", this.wordsInBoard);
		jo.put("game_finished", this.gameFinished);
		
		JSONArray words = new JSONArray();
		for(int i = 0; i < this.usedWords.size(); ++i)
			words.put(this.usedWords.get(i));
		
		JSONObject usedWords = new JSONObject();
		usedWords.put("words", words);
		
		jo.put("used_words", usedWords);
		jo.put("game_players", this.players.report());
		jo.put("game_tiles", this.tiles.report());
		jo.put("game_board", this.board.report());
		
		return jo;
	}

}
