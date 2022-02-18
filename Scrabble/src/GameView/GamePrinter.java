package GameView;

import GameLogic.Game;

public class GamePrinter {
	
	private Game game;
	public GamePrinter(Game game) {
		this.game = game;
	}

	public void showStatus(int currentTurn) {
		
		StringBuilder buffer = new StringBuilder();
		
		// Imprimos informacion del jugador
		buffer.append(game.getCurrentPlayerStatus());
		System.out.println(buffer);
		
		// Ahora imprimir el tablero
	}

}
