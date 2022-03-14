package gameLogic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import exceptions.CommandExecuteException;
import gameContainers.Board;
import gameContainers.GamePlayers;
import gameContainers.GameTiles;
import gameObjects.Box;
import gameObjects.Tile;

public class Game {
	
	private GamePlayers players;
	private Random random;
	
	private static final String tilesFile = "tiles.txt";
	private static final String boxesFile = "boxes.txt";
	private static final String wordsFile = "words.txt";
	
	private int currentTurn;
	private int numConsecutivePassedTurns;
	private int numTurnsWithoutTiles;
	
	private boolean wordsInBoard;
	private boolean gameFinished;
	
	private GameTiles tiles;
	private Board board;
	private List<String> words;
	private List<String> usedWords;
	
	private WordChecker wordChecker;
	
	public Game(GamePlayers players) {
		this.players = players;
		this.tiles = new GameTiles();
		this.tiles.loadTiles(tilesFile);
		this.board = new Board();
		this.board.loadBoard(boxesFile);
		this.words = new ArrayList<String>();
		this.loadWordList(wordsFile);
		this.random = new Random();
		this.currentTurn = 0;
		this.usedWords = new ArrayList<String>();
		this.numConsecutivePassedTurns = 0;
		this.numTurnsWithoutTiles = -1;
		this.initializePlayerTiles();
		this.wordsInBoard = false;
		this.gameFinished = false;
		this.wordChecker = new WordChecker(this);
	}

	public boolean gameIsFinished() {

		return this.gameFinished;
	}

	public String[] decideFirstTurn() {
		
		String[] lettersObtained = new String[this.getNumPlayers()];
		
		for(int i = 0; i < this.getNumPlayers(); ++i) 
			lettersObtained[i] = this.getRandomTile().getLetter();
	
		for(int i = 1; i < this.getNumPlayers(); ++i)
			if (lettersObtained[i].compareTo(lettersObtained[this.currentTurn]) < 0) 
				this.currentTurn = i;
		
		return lettersObtained;
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
	
	public String getStatus() {
		return players.getPlayerStatus(currentTurn);
	}
	
	public void assignTiles(String word, int posX, int posY, String direction) {
		
		if("V".equalsIgnoreCase(direction)) assignTilesVertical(word, posX, posY);
		else assignTilesHorizontal (word, posX, posY);
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
	
	private int getPoints(String word, int posX, int posY, String direction) {
		
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

	public int getBoardSize() {
		return board.getBoardSize();
	}

	public Box getBoxAt(int i, int j) {
		return board.getBoxAt(i,j);
	}
	
	public void update() {
		
		if (this.getRemainingTiles() == 0) 
			++numTurnsWithoutTiles;
		
		nextTurn();
		
		if(this.numConsecutivePassedTurns == this.getNumPlayers()*2)
			this.gameFinished = true;
		
		if (this.numTurnsWithoutTiles == this.getNumPlayers())
			this.gameFinished = true;
	}

	
	public boolean writeAWord(String word, int posX, int posY, String direction) {
		
		usedWords.add(word);
		Collections.sort(usedWords);
		assignTiles(word, posX, posY, direction);
		this.wordsInBoard = true;
		players.givePoints(currentTurn, getPoints(word, posX, posY, direction));
		players.drawTiles(this, currentTurn);
		numConsecutivePassedTurns = 0;
		
		return true;
	}
	
	public void passTurn() {
		++this.numConsecutivePassedTurns;
	}
	
	public boolean swapTile() {
		
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
	
	public void checkArguments(String word, int posX, int posY, String direction) throws CommandExecuteException {
		this.wordChecker.checkArguments(word, posX, posY, direction);
	}

	public GamePlayers getPlayers() {
		return this.players;
	}
	
	public int getCurrentTurn() {
		return this.currentTurn;
	}

	public void userExits() {
		this.gameFinished = true;
	}

	public boolean getWordsInBoard() {
		
		return this.wordsInBoard;
	}

	public List<String> getWordsList() {
		
		return this.words;
	}

	public List<String> getUsedWords() {
		
		return this.usedWords;
	}

	public Board getBoard() {
		
		return this.board;
	}

}
