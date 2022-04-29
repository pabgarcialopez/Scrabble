package scrabble;

import java.io.IOException;

import client.Client;
import logic.Game;
import server.Server;
import storage.GameLoader;

/* APUNTES GENERALES:
   
   La clase Main es la clase donde comienza la ejecución de la aplicación.
  
 */
public class Main {
	
	private static void start() throws IOException {

		GameLoader.initBuilders();
		Game.initWordList();
		
		Server server = new Server();
		Client client = new Client(server);
		server.setClient(client);
		client.initGUI();				
	}

	public static void main(String[] args) {
		try {
			start();
		} catch (Exception e) {
			System.err.print(e.getMessage());
		}

	}
}
