package GameView;

import GameContainers.GamePlayers;
import GameLogic.Game;
import GameUtils.StringUtils;

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
		
		// Ahora imprimir el tablero (en funcion aparte).
		
	}

	// Funcion para mostrar como ha sido la decision de quien empieza a jugar
	public void decideFirstTurn(String[] lettersObtained, GamePlayers players, int turn) {
		StringBuilder buffer = new StringBuilder();
		buffer.append("Eleccion de turnos:").append(StringUtils.LINE_SEPARATOR);
		
		for(int i = 0; i < players.getNumPlayers(); i++) {
			buffer.append(players.getPlayerName(i)).append(" ha cogido una ")
				  .append(lettersObtained[i]).append(StringUtils.LINE_SEPARATOR);
		}
		
		buffer.append("Empieza ").append(players.getPlayerName(turn)).append(StringUtils.LINE_SEPARATOR);
		
		System.out.println(buffer);
	}

	public void electionMenu() {
		StringBuilder buffer = new StringBuilder();
		
		buffer.append("Opciones de juego:").append(StringUtils.LINE_SEPARATOR)
			  .append("1. Colocar palabra.").append(StringUtils.LINE_SEPARATOR)
			  .append("2. Pasar turno.").append(StringUtils.LINE_SEPARATOR)
			  .append("3. Cambiar ficha.").append(StringUtils.LINE_SEPARATOR);
		
	}

}
