package wordCheckers;

import java.util.Map;

import exceptions.CommandExecuteException;
import logic.Game;

public class CheckerDirection implements Checker {

	/* Clase CheckerDirection:
	 * Comprueba que la dirección introducida sea vertical u horizontal.
	 * En caso contrario, se lanza una excepción
	 */
	
	@Override
	public void check(Game game, String word, int posX, int posY, String direction, Map<String, Integer> lettersNeeded)
			throws CommandExecuteException {
		if(!"V".equalsIgnoreCase(direction) && !"H".equalsIgnoreCase(direction))
			throw new CommandExecuteException("El argumento de la direccion no es válido.");
	}

}
