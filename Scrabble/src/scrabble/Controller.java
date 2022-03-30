package scrabble;

import java.io.FileNotFoundException;
import java.util.Scanner;

import command.Command;
import exceptions.GameException;
import gameLogic.Game;
import gameView.ScrabbleObserver;
import storage.GameLoader;

public class Controller {
	
	private static final String PROMPT = "Comando ([h]elp) > ";
	
	private Game game;
	
	private Scanner scanner;
	
	Controller(Game game, Scanner scanner) throws Exception {
		this.game = game;
		this.scanner = scanner;
	}
	
	Controller(Game game) {
		this.game = game;
		this.scanner = null;
	}

	public void runConsole() {
		
		if(game.humanIsPlaying()) {
			System.out.print(PROMPT);
			String s = scanner.nextLine();

			String[] parameters = s.toLowerCase().trim().split(" ");
			
			try {
				Command command = Command.getCommand(parameters);
				command.execute(game);
				pausa();
				game.update();
			}
			catch (GameException ex) {
				System.out.println(ex.getMessage());
			}
		}
		else {
			game.automaticPlay();
			pausa();
			game.update();
		}
	}
	
	public void pausa() {
		System.out.println("Pulse enter para continuar...");
		this.scanner.nextLine();
	}
	
	public void addObserver(ScrabbleObserver o) {
		this.game.addObserver(o);
	}
	
	public void removeObserver(ScrabbleObserver o) {
		this.game.removeObserver(o);
	}
	
	public void writeAWord(String word, int posX, int posY, String direction) {
		this.game.writeAWord(word, posX, posY, direction);
	}
	
	public void reset() throws FileNotFoundException {
		GameLoader.reset(game);
	}
	
	public void passTurn() {
		this.game.passTurn();
	}
	
	public void swapTile() {
		this.game.swapTile();
	}
	
	public void update() {
		this.game.update();
	}
	
	public void loadGame(String file) throws FileNotFoundException {
		GameLoader.loadGame(game, file);
	}
}
