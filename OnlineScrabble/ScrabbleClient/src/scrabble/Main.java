package scrabble;

import java.io.IOException;

import client.Client;

/* APUNTES GENERALES:
   
   La clase Main es la clase donde comienza la ejecución de la aplicación.
  
 */
public class Main {
	
	private static void start(String[] args) throws IOException {

		Client client = new Client(args[0], args[1], Integer.parseInt(args[2]));
		client.start();
	}

	public static void main(String[] args) {
		try {
			start(args);
		} catch (Exception e) {
			System.err.print(e.getMessage());
		}
	}
}
