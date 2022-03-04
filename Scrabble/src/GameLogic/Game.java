package GameLogic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import GameContainers.GamePlayers;
import GameContainers.GameTiles;
import GameObjects.Board;
import GameObjects.Box;
import GameObjects.Player;
import GameObjects.Tile;
import GameUtils.StringUtils;
import GameView.GamePrinter;

public class Game {
	
	private Scanner scanner;
	private GamePlayers players;
	private Random random;
	
	private static final String tilesFile = "tiles.txt";
	private static final String boxesFile = "boxes.txt";
	private static final String wordsFile = "words.txt";
	
	private int currentTurn;
	private int numConsecutivePassedTurns;
	private int numTurnsWithoutTiles;
	
	private boolean wordsInBoard;
	
	private GameTiles tiles;
	private Board board;
	private List<String> words;
	private List<String> usedWords;
	private GamePrinter printer;
	
	public Game(Scanner scanner) {
		this.scanner = scanner;
		this.printer = new GamePrinter(this);
		this.players = addPlayers(selectNumPlayers());
		this.printer.showInitializingMessage();
		this.tiles = new GameTiles();
		this.tiles.loadTiles(tilesFile);
		this.board = new Board();
		this.board.loadBoard(boxesFile);
		this.words = new ArrayList<String>();
		this.loadWordList(wordsFile);
		this.random = new Random();
		this.currentTurn =  this.decideFirstTurn();
		this.usedWords = new ArrayList<String>();
		this.numConsecutivePassedTurns = 0;
		this.numTurnsWithoutTiles = -1;
		this.initializePlayerTiles();
		this.wordsInBoard = false;
	}

	public boolean gameIsFinished() {
		
		if(this.numConsecutivePassedTurns == this.getNumPlayers()*2)
			return true;
		
		if (this.numTurnsWithoutTiles == this.getNumPlayers()) return true;
		
		return false;
	}
	
	private GamePlayers addPlayers(int numPlayers) {
		
		GamePlayers players = new GamePlayers();
		while(players.getNumPlayers() < numPlayers) {
			try {
				System.out.print("Nombre del jugador " + (players.getNumPlayers() + 1) + ": ");
				players.addPlayer(new Player(this.scanner.nextLine()));
			}
			
			catch(IllegalArgumentException iae) {
				System.out.println(iae.getMessage());
			}
		}
		
		return players;
	}
	
	private int selectNumPlayers() {
		
		int numPlayers = 0;
		boolean done = false;
		System.out.print("Selecciona el numero de jugadores (2-4): ");
		
		while (!done) {
			try {
				numPlayers = scanner.nextInt();
				
				if (numPlayers < 2 || numPlayers > 4) {
					System.out.println("El numero de jugadores debe estar entre 2 y 4.");
					System.out.print("Selecciona el numero de jugadores (2-4): ");
				}
				else done = true;
				
			}
			catch (InputMismatchException ime) {
				System.out.println ("La entrada debe ser un número!");
				System.out.print("Selecciona el numero de jugadores (2-4): ");
				scanner.nextLine();
			}
		}
		
		// Para que la entrada sea correcta.
		scanner.nextLine();  
		
		return numPlayers;
	}
	
	private int decideFirstTurn() {
		
		String[] lettersObtained = new String[this.getNumPlayers()];
		
		for(int i = 0; i < this.getNumPlayers(); ++i) 
			lettersObtained[i] = this.getRandomTile().getLetter();
		
		int turn = 0;
		for(int i = 1; i < this.getNumPlayers(); ++i)
			if (lettersObtained[i].compareTo(lettersObtained[turn]) < 0) 
				turn = i;
		
		printer.showFirstTurn(lettersObtained, players, turn);
		
		return turn;		
	}
	
	private Double getRandomDouble() {
		return this.random.nextDouble();
	}
	
	private int getNumPlayers() {
		return this.players.getNumPlayers();
	}
	
	private void nextTurn() {
		this.currentTurn = (this.currentTurn + 1) % this.getNumPlayers();
	}
	
	private void loadWordList(String file) {
		
		try(BufferedReader buffer = new BufferedReader(new FileReader(file))) {
			String linea = null;
			while((linea = buffer.readLine()) != null) {
				linea = linea.trim();
				this.words.add(linea);
			}
		}
		catch (IOException ioe) {
			throw new IllegalArgumentException("Error al leer el fichero words.txt", ioe);
		}
	}
	
	public Tile getRandomTile() {
		if(tiles.getNumTiles() == 0)
			return null;
		
		return 
			tiles.getTile((int) (this.getRandomDouble() * this.tiles.getNumTiles()));
	}
	
	public void removeTile(Tile tile) {
		tiles.remove(tile);
	}
	
	public void initializePlayerTiles() {
		for(int i = 0; i < this.players.getNumPlayers(); i++) {
			players.drawTiles(this, i);
		}
	}

	public void printStatus() {
		printer.showStatus(players.getPlayerStatus(currentTurn));
	}
	
	public void printBoard() {
		printer.showBoard();
	}

	public void printEndMessage() {
		this.printer.showEndMessage();
	}
	
	public void assignTiles(String[] arguments) {
		
		int posX = Integer.parseInt(arguments[2]), posY = Integer.parseInt(arguments[3]);
		
		if(arguments[1].equals("V")) assignTilesVertical(arguments[0], posX, posY);
		else assignTilesHorizontal (arguments[0], posX, posY);
	}
	
	private void assignTilesVertical(String word, int posX, int posY) {
		
		for (int i = 0; i < word.length(); ++i) {
		
			Tile tile = this.players.getPlayerTile(this.currentTurn, String.valueOf(word.charAt(i)));
			
			try {
				this.board.assignTile(tile, posX + i, posY);
				this.players.removePlayerTile(this.currentTurn, tile);
			}
			catch (IllegalArgumentException iae) {};
		}
	}
	
	private void assignTilesHorizontal(String word, int posX, int posY) {
		
		for (int i = 0; i < word.length(); ++i) {
		
			Tile tile = this.players.getPlayerTile(this.currentTurn, String.valueOf(word.charAt(i)));
			
			try {
				this.board.assignTile(tile, posX, posY + i);
				this.players.removePlayerTile(this.currentTurn, tile);
			}
			catch (IllegalArgumentException iae) {};
		}
	}

	public int getRemainingTiles() {
		return this.tiles.getNumTiles();
	}
	
	private int getPoints(String[] arguments) {
		
		int posX = Integer.parseInt(arguments[2]), posY = Integer.parseInt(arguments[3]);
		
		int points = getLettersPoints(arguments[0], posX, posY, arguments[1]);		
		
		points = pointsAfterWordMultiplier(arguments[0], posX, posY, arguments[1], points);
		
		return points;
	}
	
	private int getLettersPoints(String word, int posX, int posY, String direction) {
		
		int points = 0;
		
		if ("V".equals(direction)) {
			for (int i = 0; i < word.length(); ++i) {
				points += this.board.getPoints(posX + i, posY);
			}
		}
		else {
			for (int i = 0; i < word.length(); ++i) {
				points += this.board.getPoints(posX, posY + i);
			}
		}
		
		return points;
	}
	
	private int pointsAfterWordMultiplier(String word, int posX, int posY, String direction, int points) {
		
		if ("V".equals(direction)) {
			for (int i = 0; i < word.length(); ++i) {
				points *= this.board.getWordMultiplier(posX + i, posY);
			}
		}
		else {
			for (int i = 0; i < word.length(); ++i) {
				points *= this.board.getWordMultiplier(posX, posY + i);
			}
		}
		
		return points;
	}

	public int getBoardSize() {
		return board.getBoardSize();
	}

	public Box getBoxAt(int i, int j) {
		return board.getBoxAt(i,j);
	}


	public boolean isCentre(int i, int j) {
		return board.isCentre(i, j);
	}
	
	public boolean play() {
		
		// Devuelve true si se ha jugado el turno y false en caso contrario.
		
		int election = electionMenu();
		
		switch(election) {
			
			// Jugador pone palabra
			case 1: {
				if (!writeAWord()) return false;
				break;
			}
			
			// Jugador pasa de turno.
			case 2: {
				passTurn();
				break;
			}
			
			// Cambiar ficha aleatoria por otra.
			case 3: {
				if (!swapTile()) return false;
				break;
			}
		}
		
		if (this.getRemainingTiles() == 0) 
			++numTurnsWithoutTiles;
		
		nextTurn();
		
		return true;
	}
	
	private int electionMenu() {
		printer.showElectionMenu();
		boolean done = false;
		int election = 0;
		
		while(!done) {
			try  {
				System.out.print("Elige opcion: ");
				election = scanner.nextInt();
				
				if (0 < election && election < 4) done = true;
				else System.out.println("Opcion no valida.");
			}
			catch (InputMismatchException ime) {
				System.out.println("Opcion no valida.");
				scanner.nextLine();
			}
		}
		
		scanner.nextLine();
		
		return election;
	}
	
	private boolean writeAWord() {
		
		String arg = askArguments();
		
		// arguments[0] es la palabra; arguments[1] es la direccion;
		// arguments[2] es la coordenada x (fila); arguments[3] es la coordenada y (columna).
		String[] arguments = arg.trim().split(" ");
		arguments[0] = arguments[0].toLowerCase();
		
		if("exit".equals(arguments[0]))
			return false;
		
		try {
			checkArguments(arguments);
			usedWords.add(arguments[0]);
			Collections.sort(usedWords);
			assignTiles(arguments);
			this.wordsInBoard = true;
			players.givePoints(currentTurn, getPoints(arguments));
			players.drawTiles(this, currentTurn);
			numConsecutivePassedTurns = 0;
		}
		catch (IllegalArgumentException iae) {
			System.out.println("Argumentos no validos. " + iae.getMessage() + StringUtils.LINE_SEPARATOR);
			return false;
		}
		
		return true;
	}
	
	private String askArguments() {
		
		String arguments;
		System.out.print("Introduce palabra (\"exit\" -> opciones),\ndireccion (V/H) y posicion en el tablero: ");
		arguments = scanner.nextLine();
		
		System.out.print(StringUtils.LINE_SEPARATOR);
		
		return arguments;
	}
	
	private void passTurn() {
		++this.numConsecutivePassedTurns;
	}
	
	private boolean swapTile() {
		
		if(tiles.getSize() <= 0) {
			System.out.println("No hay fichas para robar.");
			return false;
		}
		
		int randomPlayerTile = (int) (getRandomDouble() * players.getNumPlayerTiles(this.currentTurn));
		
		// Aniadimos la ficha al saco original
		tiles.add(players.getPlayerTile(currentTurn, randomPlayerTile));
		
		// Quitamos la ficha al jugador
		players.removePlayerTile(currentTurn, randomPlayerTile);
		
		// Le damos una ficha al jugador aleatoria
		players.drawTiles(this, currentTurn);
		
		++this.numConsecutivePassedTurns;
		
		return true;
	}
	
	

	private void checkArguments(String[] arguments) {
		
		checkArgumentsLength(arguments);
		
		checkWordExists(arguments[0]);
		
		checkWordNotUsed(arguments[0]);
		
		checkDirection(arguments[1]);
		
		checkWordLength(arguments[0]);
		
		
		int posX = checkPos(arguments[2]), posY = checkPos(arguments[3]);
		
		checkPosInRange(posX, posY);
		
		Map<String, Integer> lettersNeeded = getLettersNeeded(arguments[0]);
		
		checkWordInPosAndDirection(arguments[0], posX, posY, arguments[1], lettersNeeded);
		
		checkEnoughLetters(lettersNeeded);
		
		if (!this.wordsInBoard) checkWordInCentre(arguments[0], posX, posY, arguments[1]);
		else checkWordNextToOther(arguments[0], posX, posY, arguments[1]);
		
	}
	
	private void checkArgumentsLength(String[] arguments) {
		if(arguments.length != 4) 
			throw new IllegalArgumentException("El número de argumentos introducidos no es correcto.");
	}
	
	private void checkWordExists(String word) {
		if(Collections.binarySearch(words, word) < 0)
			throw new IllegalArgumentException("La palabra introducida no existe.");
	}
	
	private void checkWordNotUsed(String word) {
		if(Collections.binarySearch(usedWords, word) >= 0)
			throw new IllegalArgumentException("La palabra introducida ya se encuentra en el tablero.");
	}
	
	private void checkDirection(String direction) {
		if(!"V".equalsIgnoreCase(direction) && !"H".equalsIgnoreCase(direction))
			throw new IllegalArgumentException("El argumento de la direccion no es válido.");
	}
	
	private void checkWordLength(String word) {
		if (word.length() > board.getBoardSize())
			throw new IllegalArgumentException("La palabra introducida es demasiado larga para entrar en el tablero.");
	}
	
	private int checkPos(String pos) {
		int posInt;
		
		try {
			posInt = Integer.parseInt(pos);
		}
		catch(NumberFormatException nfe) {
			throw new IllegalArgumentException("Las coordenadas deben ser numeros.");
		}
		
		return posInt;
	}
	
	private void checkPosInRange(int posX, int posY) {
		if(posX < 0 || posX > board.getBoardSize() - 1 || posY < 0 || posY > board.getBoardSize() - 1)
			throw new IllegalArgumentException("La palabra se sale del tablero.");
	}
	
	private Map<String, Integer> getLettersNeeded(String word) {
		
		Map<String, Integer> lettersNeeded = new HashMap<String, Integer>();
		for (int i = 0; i < word.length(); ++i) {
			String letter = "";
			letter += word.charAt(i);
			if (lettersNeeded.containsKey(letter)) lettersNeeded.put(letter, lettersNeeded.get(letter) + 1);
			else lettersNeeded.put(letter, 1);
		}
		
		return lettersNeeded;
	}
	
	private void checkLetterInPos(String letter, int posX, int posY, Map<String, Integer> lettersNeeded) {
		
		checkPosInRange(posX, posY);
		
		if (!this.players.playerHasLetter(this.currentTurn, letter) && this.board.getTile(posX, posY) == null)
				throw new IllegalArgumentException("No tienes la letra \"" + letter + "\" y no se encuentra en la casilla indicada.");
		
		if (this.board.getTile(posX, posY) != null && !this.board.getTile(posX, posY).getLetter().equalsIgnoreCase(letter))
			throw new IllegalArgumentException(String.format("En la casilla (%s,%s) está la letra %s que no coincide con tu palabra.", posX, posY, this.board.getTile(posX, posY).getLetter()));
		
		if (this.board.getTile(posX, posY) != null)
			lettersNeeded.put(letter, lettersNeeded.get(letter) - 1);
	}
	
	private void checkWordInPosAndDirection(String word, int posX, int posY, String direction, Map<String, Integer> lettersNeeded) {
		
		if ("V".equals(direction)) checkWordInPosVertical(word, posX, posY, lettersNeeded);
		else checkWordInPosHorizontal(word, posX, posY, lettersNeeded);
	}
	
	private void checkWordInPosVertical(String word, int posX, int posY, Map<String, Integer> lettersNeeded) {
		
		for (int i = 0; i < word.length(); ++i) {
			checkLetterInPos(String.valueOf(word.charAt(i)), posX + i, posY, lettersNeeded);
		}
	}
	
	private void checkWordInPosHorizontal(String word, int posX, int posY, Map<String, Integer> lettersNeeded) {
		
		for (int i = 0; i < word.length(); ++i) {
			checkLetterInPos(String.valueOf(word.charAt(i)), posX, posY + i, lettersNeeded);
		}
	}
	
	private void checkEnoughLetters(Map<String, Integer> lettersNeeded) {
		
		for (String letter : lettersNeeded.keySet()) {
			if (lettersNeeded.get(letter) > 0 
					&& this.players.numberOfTilesOf(this.currentTurn, letter) < lettersNeeded.get(letter))
				throw new IllegalArgumentException("No tienes suficientes letras para colocar la palabra.");
		}
	}
	
	private void checkWordInCentre(String word, int posX, int posY, String direction) {
		
		if ("V".equals(direction)) checkWordInCentreVertical(word, posX, posY);
		else checkWordInCentreHorizontal(word, posX, posY);
	}
	
	private void checkWordInCentreVertical(String word, int posX, int posY) {

		for (int i = 0; i < word.length(); ++i) {
			if (this.board.isCentre(posX + i, posY)) return;
		}
		
		throw new IllegalArgumentException("La primera palabra introducida en el tablero debe situarse en la casilla central.");
	}
	
	private void checkWordInCentreHorizontal(String word, int posX, int posY) {

		for (int i = 0; i < word.length(); ++i) {
			if (this.board.isCentre(posX, posY + i)) return;
		}
		
		throw new IllegalArgumentException("La primera palabra introducida en el tablero debe situarse en la casilla central.");
	}
	
	private void checkWordNextToOther(String word, int posX, int posY, String direction) {
		
		if ("V".equals(direction)) checkWordNextToOtherVertical(word, posX, posY);
		else checkWordNextToOtherHorizontal(word, posX, posY);
	}
	
	private void checkWordNextToOtherVertical(String word, int posX, int posY) {
		
		for (int i = 0; i < word.length(); ++i) {
			if (this.board.getTile(i + posX, posY) != null) return;
		}
		
		throw new IllegalArgumentException("La palabra introducida debe cortarse con alguna de las que ya están en el tablero.");
	}
	
	private void checkWordNextToOtherHorizontal(String word, int posX, int posY) {
		
		for (int i = 0; i < word.length(); ++i) {
			if (this.board.getTile(posX, i + posY) != null) return;
		}
		
		throw new IllegalArgumentException("La palabra introducida debe cortarse con alguna de las que ya están en el tablero.");
	}
	

}
