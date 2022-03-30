package scrabble;

import java.util.Scanner;

import gameView.GamePrinter;
import storage.GameLoader;

public class Main {

	public static void main(String[] args) {

		try {
			GameLoader.initBuilders();
			
			Scanner scanner = new Scanner(System.in);
			
			Controller controller = new Controller(GameLoader.initGame(scanner), scanner);
			new GamePrinter(controller);
			controller.run();
		}
		
		catch(Exception e) {
			
			e.printStackTrace();
		}		
	}
}
