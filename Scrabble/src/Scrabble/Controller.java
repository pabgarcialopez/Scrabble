package Scrabble;

import java.util.Scanner;

import GameLogic.Game;

public class Controller {
	
	private Game game;
	
	Controller() {
		this.game = new Game(new Scanner(System.in));
	}
	
	public void run() {
		
		while(!game.gameIsFinished()) {
			
			game.showStatus(); // Imprimimos estado del jugador y tablero.
			while(!game.play());
		}
		
		game.showEndMessage();
		
	}
}
