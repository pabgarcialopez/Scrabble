package simulatedObjects;

import logic.Game;

public class HumanStrategy implements Strategy {
	
	@Override
	public void play(Game game) {
		game.commandNeeded();
	}
}
