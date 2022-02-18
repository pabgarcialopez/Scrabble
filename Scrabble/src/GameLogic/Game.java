package GameLogic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
	private int currentTurn;
	private int numConsecutivePasses;
	private GameTiles tiles;
	private static final String tilesFile = "tiles.txt";
	private static final String boxesFile = "boxes.txt";
	private static final String wordsFile = "words.txt";
	private Board board;
	private Random random;
	private List<String> wordsList;
	private GamePrinter printer;
	
	public Game(Scanner scanner) {
		this.scanner = scanner;
		this.players = addPlayers(selectNumPlayers());
		this.tiles = new GameTiles();
		this.tiles.loadTiles(tilesFile);
		this.board = new Board();
		this.board.loadBoard(boxesFile);
		this.wordsList = new ArrayList<String>();
		this.loadWordList(wordsFile);
		this.random = new Random();
		this.currentTurn =  this.decideFirstTurn();
		this.numConsecutivePasses = 0;
		this.printer = new GamePrinter(this);
		
		// Inicializamos las fichas de los jugadores.
		this.initializePlayerTiles();
	}
	
	public boolean gameIsFinished() {
		if(this.numConsecutivePasses == this.getNumPlayers()*2)
			return true;
		
		return false;
	}
	
	private GamePlayers addPlayers(int numPlayers) {
		
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
		for(int i = 0; i < this.getNumPlayers(); ++i) {
			lettersObtained[i] = this.getRandomTile().getLetter();
		}
		
		int turn = 0;
		for(int i = 1; i < this.getNumPlayers(); ++i)
			if (lettersObtained[i].compareTo(lettersObtained[turn]) < 0) 
				turn = i;
		
		// TODO: Imprimir las letras sacadas.
		
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
				this.wordsList.add(linea);
			}
		}
		catch (IOException ioe) {
			throw new IllegalArgumentException("The file of words is not valid", ioe);
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
