package gameLogic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import exceptions.CommandExecuteException;
import gameContainers.Board;
import gameContainers.GamePlayers;
import gameContainers.GameTiles;
import gameObjects.Box;
import gameObjects.Tile;
import gameUtils.StringUtils;
import gameView.GamePrinter;
import gameView.ScrabbleObserver;
import storage.GameLoader;

public class Game {
	
	private GamePlayers players;
	private Random random;
	
	private int currentTurn;
	private int numConsecutivePassedTurns;
	
	private boolean wordsInBoard;
	private boolean gameFinished;
	
	private GameTiles tiles;
	private Board board;
	private List<String> usedWords;
	private static List<String> words = GameLoader.loadWordList();

	private WordChecker wordChecker;
	
	private List<ScrabbleObserver> observers;
	
	public Game(int currentTurn, int numConsecutivePassedTurns, boolean wordsInBoard,
			boolean gameFinished, GamePlayers players, GameTiles tiles, Board board, List<String> usedWords) {
		
		reset(currentTurn, numConsecutivePassedTurns, wordsInBoard, 
				gameFinished, players, tiles, board, usedWords);
		
		this.wordChecker = new WordChecker(this);
		
		this.observers = new ArrayList<ScrabbleObserver>();
	}
	
	public void reset(int currentTurn, int numConsecutivePassedTurns, boolean wordsInBoard,
			boolean gameFinished, GamePlayers players, GameTiles tiles, Board board, List<String> usedWords) {
		
		this.numConsecutivePassedTurns = numConsecutivePassedTurns;
		this.wordsInBoard = wordsInBoard;
		this.gameFinished = gameFinished;
		this.players = players;
		this.tiles = tiles;
		this.board = board;
		this.usedWords = usedWords;
		this.random = new Random();
		this.currentTurn = currentTurn;
		decideFirstTurn();
		this.initializePlayerTiles();
	}

	public boolean gameIsFinished() {
		return this.gameFinished;
	}

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
		
		GamePrinter.showFirstTurn(lettersObtained, this.players, this.currentTurn);
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
		String status = "Fichas restantes: " + this.getRemainingTiles() + StringUtils.LINE_SEPARATOR;
		status += players.getPlayerStatus(currentTurn);
		return status;
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
		
		if(this.getRemainingTiles() == 0 && this.players.getNumPlayerTiles(this.currentTurn) == 0)
			this.gameFinished = true;
		
		nextTurn();
		
		if(this.numConsecutivePassedTurns == this.getNumPlayers()*2)
			this.gameFinished = true;
	}

	public boolean writeAWord(String word, int posX, int posY, String direction) throws CommandExecuteException {
		
		this.wordChecker.checkArguments(word, posX, posY, direction);
		
		addUsedWord(word.toLowerCase());
		
		int numPlayerTilesBefore = this.players.getNumPlayerTiles(this.currentTurn);
		assignTiles(word, posX, posY, direction);
		System.out.println(String.format("El jugador %s escribe la palabra \"%s\" en la casilla (%s, %s) con dirección \"%s\".%n", this.players.getPlayerName(this.currentTurn), word.toUpperCase(), posX, posY, direction.toUpperCase()));
		
		players.givePoints(currentTurn, getPoints(word, posX, posY, direction));
		
		if(numPlayerTilesBefore == 7 && this.players.getNumPlayerTiles(this.currentTurn) == 0)
			players.giveExtraPoints(currentTurn);
		
		this.wordsInBoard = true;
		numConsecutivePassedTurns = 0;
		
		players.drawTiles(this, currentTurn);
		
		update();
		
		return true;
	}
	
	public void passTurn() {
		++this.numConsecutivePassedTurns;
		update();
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

		update();
		
		return true;
	}
	
	public void addUsedWord(String word) {
		this.usedWords.add(word);
		Collections.sort(this.usedWords);
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
	
	
	public boolean humanIsPlaying() {
		return players.humanIsPlaying(currentTurn);
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

	public void automaticPlay() {
		this.players.automaticPlay(this.currentTurn, this);
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
}
