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
			
			game.printBoard(); // Imprimimos el tablero.
			game.printStatus(); // Imprimimos estado del jugador.
			while(!game.play());
		}
		
		game.printEndMessage();
	}
}
