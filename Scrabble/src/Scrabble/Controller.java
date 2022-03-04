package Scrabble;

import java.util.InputMismatchException;
import java.util.Scanner;

import GameContainers.GamePlayers;
import GameLogic.Game;
import GameObjects.Player;
import GameView.GamePrinter;
import command.Command;
import exceptions.GameException;

public class Controller {
	
	private static final String PROMPT = "Comando ([a]yuda) > ";
	
	private Game game;
	
	private Scanner scanner;
	
	private GamePrinter printer;
	
	Controller(Scanner scanner) {
		this.scanner = scanner;
		this.game = new Game(createPlayers());
		this.printer = new GamePrinter(this.game);
	}
	
	public void run() {
		
		boolean refreshDisplay = true;
		
		printer.showFirstTurn(game.decideFirstTurn(), game.getPlayers(), game.getCurrentTurn());
		
		while(!game.gameIsFinished()) {
			
			if (refreshDisplay) {
				printer.showBoard();
				printer.showStatus();
			}
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
	
	private GamePlayers createPlayers() {
		
		int numPlayers = selectNumPlayers();
		
		GamePlayers players = new GamePlayers();
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
		
		int numPlayers = 0;
		boolean done = false;
		System.out.print("Selecciona el numero de jugadores (2-4): ");
		
		while (!done) {
			try {
				numPlayers = scanner.nextInt();
				
				if (numPlayers < 2 || numPlayers > 4) {
					System.out.println("El numero de jugadores debe estar entre 2 y 4.");
					System.out.print("Selecciona el numero de jugadores (2-4): ");
				}
				else done = true;
				
			}
			catch (InputMismatchException ime) {
				System.out.println ("La entrada debe ser un número!");
				System.out.print("Selecciona el numero de jugadores (2-4): ");
				scanner.nextLine();
			}
		}
		
		// Para que la entrada sea correcta.
		scanner.nextLine();  
		
		return numPlayers;
	}
}
