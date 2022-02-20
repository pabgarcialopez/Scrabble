package GameLogic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import GameContainers.GamePlayers;
import GameContainers.GameTiles;
import GameObjects.Board;
import GameObjects.Player;
import GameObjects.Tile;
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
	
	private GameTiles tiles;
	private Board board;
	private List<String> words;
	private List<String> usedWords;
	private GamePrinter printer;
	
	public Game(Scanner scanner) {
		this.scanner = scanner;
		this.printer = new GamePrinter(this);
		this.players = addPlayers(selectNumPlayers());
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
		
		// Inicializamos las fichas de los jugadores.
		this.initializePlayerTiles();
	}
	
	public void play() {
		
		int election = electionMenu();
		
		switch(election) {
			
			// Jugador pone palabra
			case 1: {
				// Hacer este caso diferente si se quiere encadenar palabra????
				String arg = askArguments();
				
				// arguments[0] es la palabra; arguments[1] es la direccion;
				// arguments[2] es la coordenada x (fila); arguments[3] es la coordenada y (columna).
				String[] arguments = arg.trim().split(" ");
				
				while(!validArguments(arguments)) {
					System.out.println("Argumentos no validos.");
					arg = askArguments();
					arguments = arg.trim().split(" ");
				}
				
				// Poner ahora la palabra en el tablero etc. (usedWords)
				usedWords.add(arguments[0]);
				Collections.sort(usedWords); // Para encontrar las palabras mas rapido.
				
				this.numConsecutivePassedTurns = 0;
				break;
			}
			
			// Jugador pasa de turno.
			case 2: {
					
				++this.numConsecutivePassedTurns;
				break;
			}
			
			case 3: {
				
				break;
			}
		}
		
		nextTurn();
	}
	
	private boolean validArguments(String[] arguments) {
		
		if(arguments.length != 4) 
			return false;
		
		else if(!wordExists(arguments[0], words))
			return false;
		
		else if(wordExists(arguments[0], usedWords))
			return false;
		
		else if(arguments[1] != "V" || arguments[1] != "H")
			return false;
		
		else if (Integer.parseInt(arguments[2]) < 0 || Integer.parseInt(arguments[2]) > board.getBoardSize() ||
				 Integer.parseInt(arguments[3]) < 0 || Integer.parseInt(arguments[3]) > board.getBoardSize()) {
			return false;
		}
		
		// Aqui vemos que la palabra quepa en el tablero verticalmente
		else if(arguments[1] == "V") {
			if(Integer.parseInt(arguments[2]) - arguments[0].length() < 0 ||
			   Integer.parseInt(arguments[2]) + arguments[0].length() > board.getBoardSize())
				return false;
		}
		
		// Aqui vemos que la palabra quepa en el tablero horizontalmente
		else if(arguments[1] == "H") {
			if(Integer.parseInt(arguments[3]) - arguments[0].length() < 0 ||
			   Integer.parseInt(arguments[3]) + arguments[0].length() > board.getBoardSize())
				return false;
		}
		
		return true;
	}

	private String askArguments() {
		
		String arguments;
		System.out.print("Introduce palabra, direccion (V/H) y posicion en el tablero: ");
		arguments = scanner.nextLine();
		
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
				end = half + 1;
			else if(word.compareTo(listOfWords.get(half)) > 0)
				initial = half;
			else {
				found = true;
				initial = end; // Para salir del bucle.
			}	
		}
		
		return found;
	}

	private int electionMenu() {
		printer.electionMenu();
		System.out.println("Elige opcion:");
		int election = scanner.nextInt();
		
		while(election < 1 || election > 3) {
			System.out.println("Opcion no valida.");
			System.out.println("Elige opcion: ");
			election = scanner.nextInt();
		}
		
		scanner.nextLine();
		
		return election;
	}

	public boolean gameIsFinished() {
		if(this.numConsecutivePassedTurns == this.getNumPlayers()*2)
			return true;
		
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

}
