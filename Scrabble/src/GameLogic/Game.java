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

public class Game {
	
	private boolean gameIsFinished;
	private GamePlayers players;
	private Scanner scanner;
	private int currentTurn;
	private GameTiles tiles;
	private static String tilesFile = "tiles.txt";
	private static String boxesFile = "boxes.txt";
	private static String wordsFile = "words.txt";
	private Board board;
	private Random rdm;
	private List<String> wordsList;
	
	public Game(Scanner scanner) {
		this.gameIsFinished = false;
		this.scanner = scanner;
		this.players = addPlayers(selectNumPlayers());
		this.tiles = new GameTiles();
		this.tiles.loadTiles(tilesFile);
		this.board = new Board();
		this.board.loadBoard(boxesFile);
		this.rdm = new Random();
		this.currentTurn =  this.decideFirstTurn();
		this.wordsList = new ArrayList<String>();
		this.loadWordList(wordsFile);
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
}
