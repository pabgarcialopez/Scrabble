package GameView;

import GameObjects.Box;

import GameContainers.GamePlayers;
import GameLogic.Game;
import GameUtils.StringUtils;
import SpecialEffects.SpecialEffects;

public class GamePrinter {
	
	private static final String CENTRE_SYMBOL = "*";
	private static final String DOUBLE_LETTER_SYMBOL = "•";
	private static final String DOUBLE_WORD_SYMBOL = "░";
	private static final String TRIPLE_LETTER_SYMBOL = "▒";
	private static final String TRIPLE_WORD_SYMBOL = "█";
	
	
	private Game game;
	public GamePrinter(Game game) {
		this.game = game;
	}
	
	

	public void showStatus(String status) {
		System.out.println(status);
	}

	// Funcion para mostrar como ha sido la decision de quien empieza a jugar
	public void showFirstTurn(String[] lettersObtained, GamePlayers players, int turn) {
		StringBuilder buffer = new StringBuilder();
		buffer.append("Eleccion de turnos:").append(StringUtils.LINE_SEPARATOR);
		
		for(int i = 0; i < players.getNumPlayers(); i++) {
			buffer.append(players.getPlayerName(i)).append(" ha cogido una ")
				  .append(lettersObtained[i]).append(StringUtils.LINE_SEPARATOR);
		}
		
		buffer.append(StringUtils.LINE_SEPARATOR).append("El orden de juego es: ");
		
		for(int i = 0; i < players.getNumPlayers(); i++) {
			buffer.append(players.getPlayerName((i + turn) % players.getNumPlayers()));
			if(i != players.getNumPlayers() - 1) buffer.append(" -> ");
		}
			
		
		buffer.append(StringUtils.LINE_SEPARATOR);
		
		System.out.println(buffer);
	}

	public void showElectionMenu() {
		StringBuilder buffer = new StringBuilder();
		
		buffer.append("Opciones de juego:").append(StringUtils.LINE_SEPARATOR)
			  .append("1. Colocar palabra.").append(StringUtils.LINE_SEPARATOR)
			  .append("2. Pasar turno.").append(StringUtils.LINE_SEPARATOR)
			  .append("3. Cambiar ficha.").append(StringUtils.LINE_SEPARATOR);
		
		System.out.println(buffer);
		
	}

	public void showBoard() {
		
		System.out.print("   "); // Espacio de indentacion
		// Imprimimos linea de coordenadas del lado superior
		for(int i = 0; i < game.getBoardSize(); i++) {
			if(i < 10) System.out.print(" " + i + "  ");
			else System.out.print(" " + i + " ");
		}
		
		System.out.print(StringUtils.LINE_SEPARATOR);
		
		// Imprimimos el resto del tablero
		for(int i = 0; i < game.getBoardSize(); i++) {
			
			System.out.print("   "); // Espacio de indentacion
			for(int k = 0; k < game.getBoardSize(); k++)
				System.out.print("----");
			
			System.out.print(StringUtils.LINE_SEPARATOR);
				
			// Imprimir numero de fila
			if(i < 10) System.out.print(" " + i);
			else System.out.print(i);
			
			// Imprimir lo correspondiente a esa fila
			for(int j = 0; j < game.getBoardSize(); j++) {
				Box box = game.getBoxAt(i, j);
				String boxContent = box.toString();
				
				if(" ".equals(boxContent)) {
					SpecialEffects speEff = box.getSpecialEffect();
					
					if(SpecialEffects.CENTRE.equals(speEff))
						boxContent = CENTRE_SYMBOL;
					else if(SpecialEffects.DOUBLE_LETTER.equals(speEff))
						boxContent = DOUBLE_LETTER_SYMBOL;
					else if(SpecialEffects.DOUBLE_WORD.equals(speEff))
						boxContent = DOUBLE_WORD_SYMBOL;
					else if(SpecialEffects.TRIPLE_LETTER.equals(speEff))
						boxContent = TRIPLE_LETTER_SYMBOL;
					else if(SpecialEffects.TRIPLE_WORD.equals(speEff))
						boxContent = TRIPLE_WORD_SYMBOL;
				}
				
				System.out.print("|" + " " + boxContent + " ");
			}
				
			System.out.print("|" + StringUtils.LINE_SEPARATOR);
		}
		
		System.out.print("   "); // Espacio de indentacion
		for(int k = 0; k < game.getBoardSize(); k++)
			System.out.print("----");
		
		System.out.print(StringUtils.LINE_SEPARATOR);
		
		System.out.print("Doble letra: " + DOUBLE_LETTER_SYMBOL + " || " +
				         "Doble palabra: " + DOUBLE_WORD_SYMBOL + " || " +
				         "Triple letra: " + TRIPLE_LETTER_SYMBOL + " || " + 
				         "Triple palabra: " + TRIPLE_WORD_SYMBOL + 
				         StringUtils.LINE_SEPARATOR);
		
		System.out.print(StringUtils.LINE_SEPARATOR);
	}



	public void showEndMessage() {
		System.out.println("Gracias por jugar!");
	}



	public void showInitializingMessage() {
		
		System.out.print(StringUtils.LINE_SEPARATOR + "Inicializando juego..." + StringUtils.DOUBLE_LINE_SEPARATOR);
	}
	

}