package command;

import scrabble.Controller;

// Ver apuntes de la clase padre Command.
public class HelpCommand extends Command {

	private static final String NAME = "help";

	private static final String DETAILS = "[h]elp";

	private static final String SHORTCUT = "h";

	private static final String HELP = "muestra esta ayuda";

	public HelpCommand() {
		super(NAME, SHORTCUT, DETAILS, HELP);
	}

	/* Sobrescritura del método execute:
	 * Recoge e imprime por consola la descripción de todos los comandos disponibles.
	 */
	
	@Override
	public boolean execute(Controller controller) {
		
		controller.printHelpMessage(AVAILABLE_COMMANDS);
		return true;
	}
}
