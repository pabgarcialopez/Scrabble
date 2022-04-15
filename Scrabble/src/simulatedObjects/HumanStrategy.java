package simulatedObjects;

import java.util.List;

import logic.Game;
import logic.WordChecker;

public class HumanStrategy implements Strategy {
	
	@Override
	public void play(Game game, WordChecker wordChecker, List<Tile> tilesForWord) {
		game.movementNeeded();
	}
}
