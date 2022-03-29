package command;

import gameLogic.Game;

public class SwapTileCommand extends Command {
	
	private static final String NAME = "intercambio";

	private static final String DETAILS = "[i]ntercambio";

	private static final String SHORTCUT = "i";

	private static final String HELP = "intercambiar una ficha por una del saco";
	
	public SwapTileCommand() {
		super(NAME, SHORTCUT, DETAILS, HELP);
	}
	
	@Override
	public boolean execute(Game game) {
		
		if (!game.swapTile())
			return false;
		
		return true;
	}
}
