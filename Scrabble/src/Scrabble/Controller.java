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
			
			game.printStatus(); // Imprimimos estado del jugador.
			game.printBoard(); // Imprimimos el tablero.
			while(!game.play());
		}
		
		game.printEndMessage();
		
	}
}
