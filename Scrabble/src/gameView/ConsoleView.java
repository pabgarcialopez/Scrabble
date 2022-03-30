package gameView;

import gameContainers.GamePlayers;
import gameLogic.Game;
import gameObjects.Box;
import gameObjects.SpecialEffects;
import gameUtils.StringUtils;
import scrabble.Controller;

public class ConsoleView implements ScrabbleObserver {
	
	private static final String CENTRE_SYMBOL = "*";
	private static final String DOUBLE_LETTER_SYMBOL = "•";
	private static final String DOUBLE_WORD_SYMBOL = "░";
	private static final String TRIPLE_LETTER_SYMBOL = "▒";
	private static final String TRIPLE_WORD_SYMBOL = "█";
	
	private Controller controller;
	
	public ConsoleView(Controller controller) {
		this.controller = controller;
		this.controller.addObserver(this);
	}

	public void showStatus(Game game) {
		System.out.println(game.getStatus());
	}

	// Funcion para mostrar como ha sido la decision de quien empieza a jugar
	public static void showFirstTurn(String[] lettersObtained, GamePlayers players, int turn) {
		
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

	public void showBoard(Game game) {
		
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

	@Override
	public void onWordWritten(Game game, String word, int posX, int posY, String direction, int points,
			int extraPoints) {
		System.out.println(String.format(
				"El jugador %s escribe la palabra \"%s\" en la posicion (%s, %s) en dirección \"%s\".%n",
				game.getPlayers().getPlayerName(game.getCurrentTurn()), word.toUpperCase(), posX, posY, direction.toUpperCase()));
		System.out.print(String.format("¡Gana %s puntos!", points));
		if(extraPoints != 0)
			System.out.println(String.format(" Además, ¡gana %s puntos extra!%n", extraPoints));
		else
			System.out.println(StringUtils.LINE_SEPARATOR);
	}

	@Override
	public void onPassed(Game game) {
		System.out.println(String.format("El jugador %s pasa de turno.", game.getPlayers().getPlayerName(game.getCurrentTurn())));
	}

	@Override
	public void onSwapped(Game game) {
		System.out.println(String.format("El jugador %s intercambia una ficha.", game.getPlayers().getPlayerName(game.getCurrentTurn())));
	}

	@Override
	public void onRegister(Game game) {
		showBoard(game);
		showStatus(game);
		controller.runConsole();
	}

	@Override
	public void onReset(Game game) {}

	@Override
	public void onError(String error) {
		System.out.println(error);
	}

	@Override
	public void onUpdate(Game game) {
		showBoard(game);
		
		if(!game.gameIsFinished()) {
			showStatus(game);
			controller.runConsole();
		}
	}

	@Override
	public void onEnd() {
		showEndMessage();
		System.exit(0);
	}

}