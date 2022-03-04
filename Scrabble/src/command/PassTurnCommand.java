package command;

import GameLogic.Game;

public class PassTurnCommand extends Command {

	private static final String NAME = "pasar";

	private static final String DETAILS = "pasa[r]";

	private static final String SHORTCUT = "r";

	private static final String HELP = "pasar de turno";
	
	public PassTurnCommand() {
		super(NAME, SHORTCUT, DETAILS, HELP);
	}
	
	@Override
	public boolean execute(Game game) {
		
		game.passTurn();
		game.update();
		
		return true;
	}
	
}
