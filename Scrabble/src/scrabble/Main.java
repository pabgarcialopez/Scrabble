package scrabble;

import java.util.Scanner;

import gameUtils.StringUtils;
import storage.GameLoader;

public class Main {

	public static void main(String[] args) {

		System.out.println("Bienvenido a Scrabble!" + StringUtils.LINE_SEPARATOR);
		
		try {
			Scanner scanner = new Scanner(System.in);
			Controller controller = new Controller(GameLoader.initGame(scanner), scanner);
			controller.run();
		}
		
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
		
	}
	
	

}
