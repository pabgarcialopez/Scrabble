package scrabble;

import gameLogic.Game;
import gameView.ConsoleView;
import storage.GameLoader;

public class Main {

	public static void main(String[] args) {

		try {
			GameLoader.initBuilders();
			Game.initWordList();
			
			Controller controller = new Controller();
			new ConsoleView(controller, System.in, System.out);
		}
		
		catch(Exception e) {
			
			e.printStackTrace();
		}
	}
}