package command;

import scrabble.Controller;

public class PassTurnCommand extends Command {

	private static final String NAME = "pasar";

	private static final String DETAILS = "p[a]sar";

	private static final String SHORTCUT = "a";

	private static final String HELP = "pasar de turno";
	
	public PassTurnCommand() {
		super(NAME, SHORTCUT, DETAILS, HELP);
	}
	
	@Override
	public boolean execute(Controller controller) {
		
		controller.passTurn();
		
		return false;
	}
}
