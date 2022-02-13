package Scrabble;

import java.util.Scanner;

import GameContainers.GamePlayers;
import GameObjects.Player;

public class Controller {
	
	private Scanner scanner;
	
	Controller(Scanner scanner) {
		this.scanner = scanner;
	}
	
	public void run() {
		int numPlayers = selectNumPlayers();
		GamePlayers players = new GamePlayers();
		
		addPlayers(players, numPlayers);
		
		// Bucle principal
	}
	
	private int selectNumPlayers() {
		
		int numPlayers;
		System.out.print("Selecciona el numero de jugadores (2-4): ");
		
		numPlayers = scanner.nextInt();
		
		while(numPlayers < 2 || numPlayers > 4) {
			System.out.println("El numero de jugadores debe estar entre 2 y 4.");
			System.out.print("Selecciona el numero de jugadores (2-4): ");
			numPlayers = scanner.nextInt();
		}
		
		// Para que la entrada sea correcta.
		scanner.nextLine();  
		
		return numPlayers;
	}

	private void addPlayers(GamePlayers players, int numPlayers) {
		
		while(players.getNumPlayers() < numPlayers) {
			try {
				System.out.print("Nombre del jugador " + (players.getNumPlayers() + 1) + ": ");
				players.addPlayer(new Player(scanner.nextLine()));
			}
			
			catch(IllegalArgumentException iae) {
				System.out.println(iae.getMessage());
			}
		}
	}
}
