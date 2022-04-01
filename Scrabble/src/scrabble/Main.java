package scrabble;

import javax.swing.SwingUtilities;

import gameLogic.Game;
import gameView.ConsoleView;
import gameView.MainWindow;
import storage.GameLoader;

public class Main {

	public static void main(String[] args) {

		/*try {
			GameLoader.initBuilders();
			Game.initWordList();
			
			Controller controller = new Controller();
			new ConsoleView(controller, System.in, System.out);
		}
		
		catch(Exception e) {
			
			e.printStackTrace();
		}*/
		
		try {
			GameLoader.initBuilders();
			Game.initWordList();
			
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					new MainWindow(new Controller());
				}
				
			});
		}
		catch(Exception e) {
			
			e.printStackTrace();
		}
		
	}
}