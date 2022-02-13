package GameLogic;

import java.util.Scanner;

import GameContainers.GamePlayers;
import GameObjects.Player;

public class Game {
	
	private boolean gameIsFinished;
	private GamePlayers players;
	
	public Game(int numPlayers, Scanner scanner) {
		this.gameIsFinished = false;
		this.players = addPlayers(numPlayers);
	}
	
	public boolean gameIsFinished() {
		return gameIsFinished;
	}
	
	private GamePlayers addPlayers(int numPlayers) {
		
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
