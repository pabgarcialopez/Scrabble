package GameLogic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import GameContainers.GamePlayers;
import GameContainers.GameTiles;
import GameObjects.Board;
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
	private int numTotalTilesInGame;
	
	private GameTiles tiles;
	private Board board;
	private List<String> words;
	private List<String> usedWords;
	private GamePrinter printer;
	
	public Game(Scanner scanner) {
		this.scanner = scanner;
		this.printer = new GamePrinter(this);
		this.players = addPlayers(selectNumPlayers());
		System.out.print(StringUtils.LINE_SEPARATOR);
		this.tiles = new GameTiles();
		this.tiles.loadTiles(tilesFile);
		this.numTotalTilesInGame = this.getRemainingTiles();
		this.board = new Board();
		this.board.loadBoard(boxesFile);
		this.words = new ArrayList<String>();
		this.loadWordList(wordsFile);
		this.random = new Random();
		this.currentTurn =  this.decideFirstTurn();
		this.usedWords = new ArrayList<String>();
		this.numConsecutivePassedTurns = 0;
		this.numTurnsWithoutTiles = -1;
		
		// Inicializamos las fichas de los jugadores.
		this.initializePlayerTiles();
	}
	
	
	//Devuelve true si se ha jugado el turno y false en caso contrario.
	public boolean play() {
		
		int election = electionMenu();
		
		switch(election) {
			
			// Jugador pone palabra
			case 1: {

				String arg = askArguments();
				
				// arguments[0] es la palabra; arguments[1] es la direccion;
				// arguments[2] es la coordenada x (fila); arguments[3] es la coordenada y (columna).
				String[] arguments = arg.trim().split(" ");
				
				try {
					validArguments(arguments);
					usedWords.add(arguments[0]);
					Collections.sort(usedWords);
					this.assignTiles(arguments);
					this.players.givePoints(this.currentTurn, this.getPoints(arguments));
					this.players.drawTiles(this, this.currentTurn);
					this.numConsecutivePassedTurns = 0;
				}
				catch (IllegalArgumentException iae) {
					System.out.println("Argumentos no validos. " + iae.getMessage() + StringUtils.LINE_SEPARATOR);
					return false;
				}
				
				break;
			}
			
			// Jugador pasa de turno.
			case 2: {
					
				++this.numConsecutivePassedTurns;
				
				break;
			}
			
			case 3: {
				
				int randomPlayerTile = (int) (this.getRandomDouble() * this.players.getNumPlayerTiles(this.currentTurn));
				this.tiles.add(this.players.getPlayerTile(this.currentTurn, randomPlayerTile));
				
				this.players.removePlayerTile(this.currentTurn, randomPlayerTile);
				
				this.players.drawTiles(this, this.currentTurn);
				
				++this.numConsecutivePassedTurns;
				
				break;
			}
		}
		
		if (this.getRemainingTiles() == 0) ++this.numTurnsWithoutTiles;
		
		nextTurn();
		
		return true;
	}
	
	private void validArguments(String[] arguments) {
		
		if(arguments.length != 4) 
			throw new IllegalArgumentException("El número de argumentos introducidos no es correcto.");
		
		if(!wordExists(arguments[0], words))
			throw new IllegalArgumentException("La palabra introducida no existe.");
		
		if(wordExists(arguments[0], usedWords))
			throw new IllegalArgumentException("La palabra introducida ya se encuentra en el tablero.");
		
		if(!arguments[1].equals("V") && !arguments[1].equals("H"))
			throw new IllegalArgumentException("El argumento de la direccion no es válido.");
		
		if (arguments[0].length() > board.getBoardSize())
			throw new IllegalArgumentException("La palabra introducida es demasiado larga para entrar en el tablero.");
		
		int posX, posY;
		try {
			posX = Integer.parseInt(arguments[2]);
			posY = Integer.parseInt(arguments[3]);
		}
		catch (NumberFormatException nfe) {
			throw new IllegalArgumentException("Las coordenadas deben ser numeros.");
		}
		
		if(posX < 0 || posX > board.getBoardSize() || posY < 0 || posY > board.getBoardSize())
			throw new IllegalArgumentException("La posición en la que se quiere colocar la palabra no es válida");
		
		Map<String, Integer> numberOfEachLetterNeeded = new HashMap<String, Integer>();
		for (int i = 0; i < arguments[0].length(); ++i) {
			String letter = "";
			letter += arguments[0].charAt(i);
			if (numberOfEachLetterNeeded.containsKey(letter)) numberOfEachLetterNeeded.put(letter, numberOfEachLetterNeeded.get(letter) + 1);
			else numberOfEachLetterNeeded.put(letter, 1);
		}
		
		boolean centre = false;
		
		if(arguments[1].equals("V")) {
			
			for (int i = 0; i < arguments[0].length(); ++i) {
				
				if (i + posX > board.getBoardSize())
					throw new IllegalArgumentException("La palabra introducida se sale del tablero.");
				
				if (this.board.isCentre(posX + i, posY)) centre = true;
				
				String letter = "";
				letter += arguments[0].charAt(i);
				
				if ((!this.players.playerHasLetter(this.currentTurn, letter) || this.board.getTile(i + posX, posY) != null)
					&& (this.board.getTile(i + posX, posY) == null || !this.board.getTile(i + posX, posY).getLetter().equalsIgnoreCase(letter)))
					throw new IllegalArgumentException("La palabra no se puede colocar en la posición indicada");
				
				if (this.board.getTile(i + posX, posY) != null)
					numberOfEachLetterNeeded.put(letter, numberOfEachLetterNeeded.get(letter) - 1);
			}
			
			for (String letter : numberOfEachLetterNeeded.keySet()) {
				
				if (numberOfEachLetterNeeded.get(letter) > 0 
						&& this.players.numberOfTilesOf(this.currentTurn, letter) < numberOfEachLetterNeeded.get(letter))
					throw new IllegalArgumentException("No tienes las letras necesarias para colocar la palabra.");
			}
		}
		
		else {
			
			for (int i = 0; i < arguments[0].length(); ++i) {
				
				if (i + posY > board.getBoardSize())
					throw new IllegalArgumentException("La palabra introducida se sale del tablero.");
				
				if (this.board.isCentre(posX, posY + i)) centre = true;
				
				String letter = "";
				letter += arguments[0].charAt(i);
				
				if ((!this.players.playerHasLetter(this.currentTurn, letter) || this.board.getTile(posX, i + posY) != null)
						&& (this.board.getTile(posX, i + posY) == null || !this.board.getTile(posX, i + posY).getLetter().equalsIgnoreCase(letter)))
					throw new IllegalArgumentException("La palabra no se puede colocar en la posición indicada");
					
					if (this.board.getTile(posX, i + posY) != null)
						numberOfEachLetterNeeded.put(letter, numberOfEachLetterNeeded.get(letter) - 1);
				}
				
				for (String letter : numberOfEachLetterNeeded.keySet()) {
					
					if (numberOfEachLetterNeeded.get(letter) > 0 
							&& this.players.numberOfTilesOf(this.currentTurn, letter) < numberOfEachLetterNeeded.get(letter))
						throw new IllegalArgumentException("No tienes las letras necesarias para colocar la palabra.");
				}
		}
		
		if (this.getRemainingTiles() == (this.numTotalTilesInGame - GamePlayers.NUM_TILES * this.getNumPlayers())
				&& !centre)
			throw new IllegalArgumentException("La primera palabra introducida en el tablero debe pasar por la casilla central.");
	}

	private String askArguments() {
		
		String arguments;
		System.out.print("Introduce palabra, direccion (V/H) y posicion en el tablero: ");
		arguments = scanner.nextLine();
		
		System.out.print(StringUtils.LINE_SEPARATOR);
		
		return arguments;
	}

	// Busqueda binaria (listOfWords sirve tanto para la lista de todas las palabras
	// como para la lista de palabras ya usadas.
	private boolean wordExists(String word, List<String> listOfWords) {
		
		int initial = 0;
		int end = listOfWords.size();
		boolean found = false;
		
		while(initial < end) {
			int half = (initial + end - 1) / 2;
			if(word.compareTo(listOfWords.get(half)) < 0)
				end = half;
			else if(word.compareTo(listOfWords.get(half)) > 0)
				initial = half + 1;
			else {
				found = true;
				initial = end; // Para salir del bucle.
			}	
		}
		
		return found;
	}

	private int electionMenu() {
		printer.electionMenu();
		System.out.print("Elige opcion:");
		int election = scanner.nextInt();
		
		while(election < 1 || election > 3) {
			System.out.println("Opcion no valida.");
			System.out.print("Elige opcion: ");
			election = scanner.nextInt();
		}
		
		scanner.nextLine();
		
		return election;
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
		
		int numPlayers;
		System.out.print("Selecciona el numero de jugadores (2-4): ");
		
		numPlayers = scanner.nextInt();
		
		while(numPlayers < 2 || numPlayers > 4) {
			System.out.println("El numero de jugadores debe estar entre 2 y 4.");
			System.out.print("Selecciona el numero de jugadores (2-4): ");
			numPlayers = scanner.nextInt();
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
		
		// TODO: Imprimir las letras sacadas.
		printer.decideFirstTurn(lettersObtained, players, turn);
		
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

	public void showStatus() {
		printer.showStatus(currentTurn);
	}
	
	public String getCurrentPlayerStatus() {
		return players.getPlayerStatus(currentTurn);
	}
	
	public void assignTiles(String[] arguments) {
		
		int posX = Integer.parseInt(arguments[2]), posY = Integer.parseInt(arguments[3]);
		
		if(arguments[1].equals("V")) {
			
			for (int i = 0; i < arguments[0].length(); ++i) {
				
				String letter = "";
				letter += arguments[0].charAt(i);
				
				Tile tile = this.players.getPlayerTile(this.currentTurn, letter);
				
				try {
					this.board.assignTile(tile, i + posX, posY);
					this.players.removePlayerTile(this.currentTurn, tile);
				}
				catch (IllegalArgumentException iae) {};
			}
		}
		
		if(arguments[1].equals("H")) {
			
			for (int i = 0; i < arguments[0].length(); ++i) {
				
				String letter = "";
				letter += arguments[0].charAt(i);
				
				Tile tile = this.players.getPlayerTile(this.currentTurn, letter);
				
				try {
					this.board.assignTile(tile, posX, i + posY);
					this.players.removePlayerTile(this.currentTurn, tile);
				}
				catch (IllegalArgumentException iae) {};				
			}
		}
	}

	public int getRemainingTiles() {
		return this.tiles.getNumTiles();
	}

	public void showEndMessage() {
		this.printer.endMessage();
	}
	
	private int getPoints(String[] arguments) {
		
		int points = 0;
		int posX = Integer.parseInt(arguments[2]), posY = Integer.parseInt(arguments[3]);
		
		if (arguments[1].equals("V")) {
			for (int i = 0; i < arguments[0].length(); ++i) {
				points += this.board.getPoints(posX + i, posY);
			}
		}
		else {
			for (int i = 0; i < arguments[0].length(); ++i) {
				points += this.board.getPoints(posX, posY + i);
			}
		}
		
		return points;
	}
}
