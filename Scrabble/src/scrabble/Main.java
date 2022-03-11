package scrabble;

import java.util.Scanner;

import gameUtils.StringUtils;

public class Main {

	public static void main(String[] args) {

		System.out.println("Bienvenido a Scrabble!" + StringUtils.LINE_SEPARATOR);
		Controller controller = new Controller(new Scanner(System.in));
		controller.run();
		
	}
	
	

}
