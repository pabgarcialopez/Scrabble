package scrabble;

import java.io.IOException;

import logic.Game;
import server.Server;
import storage.GameLoader;

/* APUNTES GENERALES:
   
   La clase Main es la clase donde comienza la ejecución de la aplicación.
  
 */
public class Main {
	
	private static void start(String[] args) throws IOException {
		
		GameLoader.initBuilders();
		Game.initWordList();

		Server server = new Server(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
		server.start();
	}

	public static void main(String[] args) {
		try {
			start(args);
		} catch (Exception e) {
			System.err.print(e.getMessage());
		}
	}
}
