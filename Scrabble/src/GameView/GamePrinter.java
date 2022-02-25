package GameView;

import GameObjects.Box;

import GameContainers.GamePlayers;
import GameLogic.Game;
import GameUtils.StringUtils;
import SpecialEffects.SpecialEffects;

public class GamePrinter {
	
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
		
		buffer.append("Empieza ").append(players.getPlayerName(turn)).append(StringUtils.LINE_SEPARATOR);
		
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

	@SuppressWarnings("unlikely-arg-type")
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
						boxContent = "*";
					else if(SpecialEffects.DOUBLE_LETTER.equals(speEff))
						boxContent = "#";
					else if(SpecialEffects.DOUBLE_WORD.equals(speEff))
						boxContent = "$";
					else if(SpecialEffects.TRIPLE_LETTER.equals(speEff))
						boxContent = "&";
					else if(SpecialEffects.TRIPLE_WORD.equals(speEff))
						boxContent = "%";
				}
				
				System.out.print("|" + " " + boxContent + " ");
				//if(game.isCentre(i,j) && boxContent.equals(" ")) System.out.print("|" + " " + "*" + " ");
				//else System.out.print("|" + " " + boxContent + " ");
			}
				
			System.out.print("|" + StringUtils.LINE_SEPARATOR);
		}
		
		System.out.print("   "); // Espacio de indentacion
		for(int k = 0; k < game.getBoardSize(); k++)
			System.out.print("----");
		
		System.out.print(StringUtils.LINE_SEPARATOR);
		System.out.print("Double Letter: # || Double Word: $ || Triple Letter: & || Triple Word: %" + StringUtils.LINE_SEPARATOR);
		System.out.print(StringUtils.DOUBLE_LINE_SEPARATOR);
	}



	public void showEndMessage() {
		System.out.println("Gracias por jugar!");
	}
	

}