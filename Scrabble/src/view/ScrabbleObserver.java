package view;

import java.util.List;

import command.Command;
import logic.Game;

/* APUNTES GENERALES:
   
   La intefaz SrabbleObserver es implementada por todas aquellas clases que actúen
   de observadores. Así, cada vez que se produce un cambio en el modelo, se notifica
   a los observadores para que efectúen los cambios oportunos.
   
 */
public interface ScrabbleObserver {

	/* Método onWordWritten:
	 * Es llamado cuando una palabra ha sido escrita en el tablero.
	 */
	void onWordWritten(Game game, String word, int posX, int posY, String direction, int points, int extraPoints);
	
	/* Método onPassed:
	 * Es llamado cuando un jugador ha pasado de turno.
	 */
	void onPassed(Game game);
	
	/* Método onSwapped:
	 * Es llamado cuando un jugador ha intercambiado una ficha.
	 */
	void onSwapped(Game game);
	
	/* Método onRegister:
	 * Es llamado cuando un observador se añade a la lista de observadores.
	 */
	void onRegister(Game game);
	
	/* Método onReset:
	 * Es llamado cuando un se ejecuta el comando reset.
	 */
	void onReset(Game game);
	
	/* Método onError:
	 * Es llamado cuando un error ha ocurrido.
	 */
	void onError(String error);
	
	/* Método onUpdate:
	 * Es llamado cuando se actualiza el juego (método update de Game).
	 */
	void onUpdate(Game game);
	
	/* Método onEnd:
	 * Es llamado cuando se ha alcanzado el final de la partida.
	 */
	void onEnd(String message);
	
	/* Método onFirstTurnDecided:
	 * Es llamado cuando se ha elegido el orden de turnos de la partida.
	 */
	void onFirstTurnDecided(Game game, String[] lettersObtained);
	
	default void printHelpMessage(List<Command> AVAILABLE_COMMANDS) {}
	
	void onMovementNeeded();
}
