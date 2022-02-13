package Scrabble;

import java.util.Scanner;

import GameUtils.StringUtils;

public class Main {

	public static void main(String[] args) {

		Controller controller = new Controller(new Scanner(System.in));
		System.out.println("Bienvenido a Scrabble!" + StringUtils.LINE_SEPARATOR);
		controller.run();
		
	}
	
	

}
