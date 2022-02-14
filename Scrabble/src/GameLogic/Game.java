package GameLogic;

import java.util.Random;
import java.util.Scanner;

import GameContainers.GamePlayers;
import GameContainers.GameTiles;
import GameObjects.Board;
import GameObjects.Player;

public class Game {
	
	private boolean gameIsFinished;
	private GamePlayers players;
	private Scanner scanner;
	private int currentTurn;
	private GameTiles tiles;	
	private static String tilesFile = "tiles.txt";
	private static String boardFile = "board.txt";
	private Board board;
	private Random rdm;
	
	public Game(Scanner scanner) {
		this.gameIsFinished = false;
		this.scanner = scanner;
		this.players = addPlayers(selectNumPlayers());
		this.tiles = new GameTiles();
		this.tiles.loadTiles(tilesFile);
		this.board = new Board();
		this.board.loadBoard(boardFile);
		this.rdm = new Random();
		this.currentTurn =  this.decideFirstTurn();
	}
	
	public boolean gameIsFinished() {
		return gameIsFinished;
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
			lettersObtained[i] = this.tiles.getLetter((int) (this.getRandomDouble() * this.tiles.getNumTiles()));
		}
		
		int turn = 0;
		for(int i = 1; i < this.getNumPlayers(); ++i)
			if (lettersObtained[i].compareTo(lettersObtained[turn]) < 0) turn = i;
		
		return turn;		
	}
	
	private Double getRandomDouble() {
		return this.rdm.nextDouble();
	}
	
	private int getNumPlayers() {
		return this.players.getNumPlayers();
	}
	
	private void nextTurn() {
		this.currentTurn = (this.currentTurn + 1) % this.getNumPlayers();
	}
}
