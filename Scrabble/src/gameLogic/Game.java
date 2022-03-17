package gameLogic;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import exceptions.CommandExecuteException;
import gameContainers.Board;
import gameContainers.GamePlayers;
import gameContainers.GameTiles;
import gameObjects.Box;
import gameObjects.Tile;
import storage.GameLoader;

public class Game {
	
	private GamePlayers players;
	private Random random;
	
	private int currentTurn;
	private int numConsecutivePassedTurns;
	private int numTurnsWithoutTiles;
	
	private boolean wordsInBoard;
	private boolean gameFinished;
	
	private GameTiles tiles;
	private Board board;
	private List<String> usedWords;
	private static List<String> words = GameLoader.loadWordList();

	
	private WordChecker wordChecker;
	
	public Game(GamePlayers players) {
		//this.players = players;
		//this.tiles = new GameTiles();
		//this.tiles.loadTiles(tilesFile);
		//this.board = new Board(); // comun
		//this.board.loadBoard(boxesFile); // comun
		//this.words = new ArrayList<String>(); // comun
		//this.loadWordList(wordsFile); // comun
		//this.random = new Random(); // comun
		//this.currentTurn = 0;
		//this.usedWords = new ArrayList<String>();
		//this.numConsecutivePassedTurns = 0;
		//this.numTurnsWithoutTiles = -1;
		//this.initializePlayerTiles();
		//this.wordsInBoard = false; 
		//this.gameFinished = false;
	}

	public Game(int currentTurn, int numConsecutivePassedTurns, int numTurnsWithoutTiles, boolean wordsInBoard,
			boolean gameFinished, GamePlayers players, GameTiles tiles, Board board, List<String> usedWords) {
		
		this.random = new Random();
		
		// Hace falta ver que si currentTurn es -1, entonces hay que decidir quien empieza
		this.currentTurn = currentTurn;
		
		this.numConsecutivePassedTurns = numConsecutivePassedTurns;
		this.numTurnsWithoutTiles = numTurnsWithoutTiles;
		this.wordsInBoard = wordsInBoard;
		this.gameFinished = gameFinished;
		this.players = players;
		this.tiles = tiles;
		this.board = board;
		this.usedWords = usedWords;
		this.initializePlayerTiles();
		this.wordChecker = new WordChecker(this);
	}

	public boolean gameIsFinished() {
		return this.gameFinished;
	}

	public String[] decideFirstTurn() {
		
		if(currentTurn < 0 || currentTurn > players.getNumPlayers())
			return null;
		
		String[] lettersObtained = new String[this.getNumPlayers()];
		
		for(int i = 0; i < this.getNumPlayers(); ++i) 
			lettersObtained[i] = this.getRandomTile().getLetter();
	
		// La partida es empezada por quien obtenga la letra mas cercana a la A.
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
		return words;
	}

	public List<String> getUsedWords() {
		return this.usedWords;
	}

	public Board getBoard() {
		return this.board;
	}
}
