package scrabble;

import java.io.FileNotFoundException;

import gameContainers.GamePlayers;
import gameLogic.Game;
import gameView.ScrabbleObserver;
import storage.GameLoader;
import storage.GameSaver;

public class Controller {
	
	private Game game;
	
	private String lastFileUsed;
	
	Controller() {
		this.game = new Game();
		this.lastFileUsed = null;
	}
	
	public void addObserver(ScrabbleObserver o) {
		this.game.addObserver(o);
	}
	
	public void removeObserver(ScrabbleObserver o) {
		this.game.removeObserver(o);
	}
	
	public boolean writeAWord(String word, int posX, int posY, String direction) {
		return this.game.writeAWord(word, posX, posY, direction);
	}
	
	public void reset() throws FileNotFoundException {
		
		// lastFileUsed != null --> Partida se cargo de cierto fichero.
		if(this.lastFileUsed != null)
			loadGame(this.lastFileUsed);
		
		else newGame();
	}
	
	public void newGame() throws FileNotFoundException {
		this.game.resetPlayers();
		GameLoader.newGame(game);
		this.lastFileUsed = null;
	}
	
	
	public void loadGame(String file) throws FileNotFoundException {
		this.game.resetPlayers();
		GameLoader.loadGame(game, file);
		this.lastFileUsed = file;
	}
	
	public void passTurn() {
		this.game.passTurn();
	}
	
	public boolean swapTile() {
		return this.game.swapTile();
	}
	
	public void update() {
		this.game.update();
	}

	public void addPlayers(GamePlayers players) {
		this.game.addPlayers(players);
	}

	public void userExits() {
		game.userExits();
	}

	public void saveGame(String file) throws FileNotFoundException {
		GameSaver.saveGame(this.game, file);
	}

	public void automaticPlay() {
		if(!game.humanIsPlaying()) game.automaticPlay();
	}
}
