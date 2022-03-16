package command;

import gameLogic.Game;

public class ExitCommand extends Command {

	private static final String NAME = "salir";

	private static final String DETAILS = "[s]alir";

	private static final String SHORTCUT = "s";

	private static final String HELP = "salir del juego";
	
	public ExitCommand() {
		super(NAME, SHORTCUT, DETAILS, HELP);
	}
	
	@Override
	public boolean execute(Game game) {
		game.userExits();
		return false;
	}
}
