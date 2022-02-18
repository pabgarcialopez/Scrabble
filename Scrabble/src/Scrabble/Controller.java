package Scrabble;

import java.util.Scanner;

import GameContainers.GamePlayers;
import GameLogic.Game;
import GameObjects.Player;
import GameView.GamePrinter;

public class Controller {
	
	private Game game;
	
	Controller() {
		this.game = new Game(new Scanner(System.in));
	}
	
	public void run() {
		
		while(!game.gameIsFinished()) {
			
			game.showStatus();
			
		}
		
	}
}
