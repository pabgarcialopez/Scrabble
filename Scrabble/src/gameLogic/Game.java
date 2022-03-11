package gameLogic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import exceptions.CommandExecuteException;
import gameContainers.GamePlayers;
import gameContainers.GameTiles;
import gameObjects.Board;
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
		
		int points = getLettersPoints(word, posX, posY, direction);		
		
		points = pointsAfterWordMultiplier(word, posX, posY, direction, points);
		
		return points;
	}
	
	private int getLettersPoints(String word, int posX, int posY, String direction) {
		
		int points = 0;
		
		if ("V".equalsIgnoreCase(direction)) {
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
		
		if ("V".equalsIgnoreCase(direction)) {
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
		
		checkWordExists(word);
		
		checkWordNotUsed(word);
		
		checkDirection(direction);
		
		checkWordLength(word);
		
		checkPosInRange(posX, posY);
		
		Map<String, Integer> lettersNeeded = getLettersNeeded(word);
		
		checkWordInPosAndDirection(word, posX, posY, direction, lettersNeeded);
		
		checkEnoughLetters(lettersNeeded);
		
		if (!this.wordsInBoard) checkWordInCentre(word, posX, posY, direction);
		else checkWordNextToOther(word, posX, posY, direction);
		
	}
	
	private void checkWordExists(String word) throws CommandExecuteException {
		if(Collections.binarySearch(words, word) < 0)
			throw new CommandExecuteException("La palabra introducida no existe.");
	}
	
	private void checkWordNotUsed(String word) throws CommandExecuteException {
		if(Collections.binarySearch(usedWords, word) >= 0)
			throw new CommandExecuteException("La palabra introducida ya se encuentra en el tablero.");
	}
	
	private void checkDirection(String direction) throws CommandExecuteException {
		if(!"V".equalsIgnoreCase(direction) && !"H".equalsIgnoreCase(direction))
			throw new CommandExecuteException("El argumento de la direccion no es válido.");
	}
	
	private void checkWordLength(String word) throws CommandExecuteException {
		if (word.length() > board.getBoardSize())
			throw new CommandExecuteException("La palabra introducida es demasiado larga para entrar en el tablero.");
	}
	
	private void checkPosInRange(int posX, int posY) throws CommandExecuteException {
		if(posX < 0 || posX > board.getBoardSize() - 1 || posY < 0 || posY > board.getBoardSize() - 1)
			throw new CommandExecuteException("La palabra se sale del tablero.");
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
	
	private void checkLetterInPos(String letter, int posX, int posY, Map<String, Integer> lettersNeeded) throws CommandExecuteException {
		
		checkPosInRange(posX, posY);
		
		if (this.board.getTile(posX, posY) != null && !this.board.getTile(posX, posY).getLetter().equalsIgnoreCase(letter))
			throw new CommandExecuteException(String.format("En la casilla (%s,%s) está la letra %s que no coincide con tu palabra.", posX, posY, this.board.getTile(posX, posY).getLetter()));
		
		if (this.board.getTile(posX, posY) == null && !this.players.playerHasLetter(this.currentTurn, letter))
			throw new CommandExecuteException("No tienes la letra \"" + letter + "\" y no se encuentra en la casilla indicada.");
		
		if (this.board.getTile(posX, posY) != null)
			lettersNeeded.put(letter, lettersNeeded.get(letter) - 1);
	}
	
	private void checkWordInPosAndDirection(String word, int posX, int posY, String direction, Map<String, Integer> lettersNeeded) throws CommandExecuteException {
		
		if ("V".equalsIgnoreCase(direction)) checkWordInPosVertical(word, posX, posY, lettersNeeded);
		else checkWordInPosHorizontal(word, posX, posY, lettersNeeded);
	}
	
	private void checkWordInPosVertical(String word, int posX, int posY, Map<String, Integer> lettersNeeded) throws CommandExecuteException {
		
		for (int i = 0; i < word.length(); ++i) {
			checkLetterInPos(String.valueOf(word.charAt(i)), posX + i, posY, lettersNeeded);
		}
	}
	
	private void checkWordInPosHorizontal(String word, int posX, int posY, Map<String, Integer> lettersNeeded) throws CommandExecuteException {
		
		for (int i = 0; i < word.length(); ++i) {
			checkLetterInPos(String.valueOf(word.charAt(i)), posX, posY + i, lettersNeeded);
		}
	}
	
	private void checkEnoughLetters(Map<String, Integer> lettersNeeded) throws CommandExecuteException {
		
		for (String letter : lettersNeeded.keySet()) {
			if (lettersNeeded.get(letter) > 0 
					&& this.players.numberOfTilesOf(this.currentTurn, letter) < lettersNeeded.get(letter))
				throw new CommandExecuteException("No tienes suficientes letras para colocar la palabra.");
		}
	}
	
	private void checkWordInCentre(String word, int posX, int posY, String direction) throws CommandExecuteException {
		
		if ("V".equalsIgnoreCase(direction)) checkWordInCentreVertical(word, posX, posY);
		else checkWordInCentreHorizontal(word, posX, posY);
	}
	
	private void checkWordInCentreVertical(String word, int posX, int posY) throws CommandExecuteException {

		for (int i = 0; i < word.length(); ++i) {
			if (this.board.isCentre(posX + i, posY)) return;
		}
		
		throw new CommandExecuteException("La primera palabra introducida en el tablero debe situarse en la casilla central.");
	}
	
	private void checkWordInCentreHorizontal(String word, int posX, int posY) throws CommandExecuteException {

		for (int i = 0; i < word.length(); ++i) {
			if (this.board.isCentre(posX, posY + i)) return;
		}
		
		throw new CommandExecuteException("La primera palabra introducida en el tablero debe situarse en la casilla central.");
	}
	
	private void checkWordNextToOther(String word, int posX, int posY, String direction) throws CommandExecuteException {
		
		if ("V".equalsIgnoreCase(direction)) checkWordNextToOtherVertical(word, posX, posY);
		else checkWordNextToOtherHorizontal(word, posX, posY);
	}
	
	private void checkWordNextToOtherVertical(String word, int posX, int posY) throws CommandExecuteException {
		
		for (int i = 0; i < word.length(); ++i) {
			if (this.board.getTile(i + posX, posY) != null) return;
		}
		
		throw new CommandExecuteException("La palabra introducida debe cortarse con alguna de las que ya están en el tablero.");
	}
	
	private void checkWordNextToOtherHorizontal(String word, int posX, int posY) throws CommandExecuteException {
		
		for (int i = 0; i < word.length(); ++i) {
			if (this.board.getTile(posX, i + posY) != null) return;
		}
		
		throw new CommandExecuteException("La palabra introducida debe cortarse con alguna de las que ya están en el tablero.");
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

}
