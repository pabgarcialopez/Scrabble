package scrabble;

import java.util.InputMismatchException;
import java.util.Scanner;

import command.Command;
import exceptions.GameException;
import gameContainers.GamePlayers;
import gameLogic.Game;
import gameObjects.Player;
import gameView.GamePrinter;

public class Controller {
	
	private static final String PROMPT = "Comando ([a]yuda) > ";
	
	private Game game;
	
	private Scanner scanner;
	
	private GamePrinter printer;
	
	Controller(Scanner scanner) {
		this.scanner = scanner;
		initGame();
		this.printer = new GamePrinter(this.game);
	}
	
	private void initGame() {
		System.out.println("Opciones de inicio:");
		System.out.println("1. Nueva partida.");
		System.out.println("2. Cargar partida de fichero.");
		
		int option = 0;
		
		while(option != 1 && option != 2) {
			try {
				System.out.print("Selecciona opcion: ");
				option = scanner.nextInt();
				if(option != 1 && option != 2)
					System.out.println("Opcion no valida.");
			}
			
			catch(InputMismatchException ime) {
				scanner.nextLine();
				System.out.println("Opcion no valida.");
			}
		}
			
		if(option == 1)
			this.game = new Game(createPlayers());
		
		else {
			// Cargar.
		}
		
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
