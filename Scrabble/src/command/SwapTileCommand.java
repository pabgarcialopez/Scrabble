package command;

import GameLogic.Game;

public class SwapTileCommand extends Command {
	
	private static final String NAME = "cambio";

	private static final String DETAILS = "[c]ambio";

	private static final String SHORTCUT = "c";

	private static final String HELP = "cambiar una ficha por una del saco";
	
	public SwapTileCommand() {
		super(NAME, SHORTCUT, DETAILS, HELP);
	}
	
	@Override
	public boolean execute(Game game) {
		
		if (!game.swapTile())
			return false;
		
		game.update();
		
		return true;
	}
}
