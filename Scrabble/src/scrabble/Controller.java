package scrabble;

import java.util.Scanner;

import command.Command;
import exceptions.GameException;
import gameLogic.Game;
import gameView.GamePrinter;

public class Controller {
	
	private static final String PROMPT = "Comando ([h]elp) > ";
	
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
		
		while(!game.gameIsFinished()) {
			
			if (refreshDisplay) {
				printer.showBoard();
				printer.showStatus();
			}
			
			refreshDisplay = false;
			
			if(game.humanIsPlaying()) {
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
			else {
				game.automaticPlay();
				game.update();
				refreshDisplay = true;
				pausa();
			}
		}
		
		printer.showEndMessage();
	}
	
	public void pausa() {
		System.out.println("Pulse enter para continuar ");
		this.scanner.nextLine();
	}
}
