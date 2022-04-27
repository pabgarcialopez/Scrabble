package wordCheckers;

import java.util.Map;

import exceptions.CommandExecuteException;
import logic.Game;

public class EnoughLettersChecker implements Checker {
	
	/* Clase checkEnoughLetters:
	 * Dado el mapa de las letras necesitadas, se comprueba si el jugador tiene todas ellas.
	 * En caso contrario, se lanza una excepción.
	 */
	@Override
	public void check(Game game, String word, int posX, int posY, String direction, Map<String, Integer> lettersNeeded)
			throws CommandExecuteException {
		for (String letter : lettersNeeded.keySet()) {
			if (lettersNeeded.get(letter) > 0 
					&& game.getPlayers().numberOfTilesOf(game.getCurrentTurn(), letter) < lettersNeeded.get(letter))
				throw new CommandExecuteException("No tienes suficientes letras para colocar la palabra.");
		}
	}
}
