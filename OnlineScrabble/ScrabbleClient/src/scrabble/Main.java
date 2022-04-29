package scrabble;

import java.io.IOException;

import javax.swing.SwingUtilities;

import control.Controller;
import logic.Game;
import storage.GameLoader;
import view.GUIView;

/* APUNTES GENERALES:
   
   La clase Main es la clase donde comienza la ejecución de la aplicación.
  
 */
public class Main {
	
	private static void startGUIMode(Controller controller) throws IOException{
		
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				new GUIView(controller);
			}
			
		});
	}

	private static void start(String[] args) throws IOException {

		GameLoader.initBuilders();
		Game.initWordList();

		Controller controller = new Controller();
		
		startGUIMode(controller);
	}

	public static void main(String[] args) {
		try {
			start(args);
		} catch (Exception e) {
			System.err.print(e.getMessage());
		}

	}
}
