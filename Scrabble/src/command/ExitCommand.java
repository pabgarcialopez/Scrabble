package command;

import scrabble.Controller;

// Ver apuntes de la clase padre Command.
public class ExitCommand extends Command {

	private static final String NAME = "exit";

	private static final String DETAILS = "[e]xit";

	private static final String SHORTCUT = "e";

	private static final String HELP = "salir del juego";
	
	public ExitCommand() {
		super(NAME, SHORTCUT, DETAILS, HELP);
	}
	
	/* Sobrescritura del método execute:
	 * Provoca la terminación del juego.
	 */

	@Override
	public boolean execute(Controller controller) {
		controller.userExits();
		return false;
	}
}
