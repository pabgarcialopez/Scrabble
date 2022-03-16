package scrabble;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.Scanner;

import command.Command;
import exceptions.GameException;
import gameContainers.GamePlayers;
import gameLogic.Game;
import gameObjects.Player;
import gameView.GamePrinter;
import storage.GameLoader;

public class Controller {
	
	private static final String PROMPT = "Comando ([a]yuda) > ";
	
	private Game game;
	
	private Scanner scanner;
	
	private GamePrinter printer;
	
	Controller(Game game, Scanner scanner) throws Exception {
	
		this.game = game;
		this.scanner = scanner;
		this.printer = new GamePrinter(this.game);
	}

	public void run() {
		
		boolean refreshDisplay = true;
		
		// Esto hay que quitarlo de aqui y ponerlo en algun otro lado.
		printer.showFirstTurn(game.decideFirstTurn(), game.getPlayers(), game.getCurrentTurn());
		
		while(!game.gameIsFinished()) {
			
			if (refreshDisplay) {
				printer.showBoard();
				printer.showStatus();
			}
			
			// Esto creo que no hace falta porque ya se hace en la linea 56 si es necesario.
			refreshDisplay = false;
			
			System.out.print(PROMPT);
			String s = scanner.nextLine();

			String[] parameters = s.toLowerCase().trim().split(" ");
			
			try {
				Command command = Command.getCommand(parameters);
				refreshDisplay = command.execute(game);
			}
			
			catch (GameException ex) {
				System.out.println(ex.getMessage());
			}
		}
		
		printer.showEndMessage();
	}
	
	
	
	
}
