package strategies;

import java.util.List;

import logic.Game;
import logic.WordChecker;
import simulatedObjects.Tile;

public class HumanStrategy implements Strategy {
	
	@Override
	public void play(Game game, WordChecker wordChecker, List<Tile> tilesForWord) {
		game.movementNeeded();
	}
	
	@Override
	public String toString() {
		return "human_strategy";
	}
}
