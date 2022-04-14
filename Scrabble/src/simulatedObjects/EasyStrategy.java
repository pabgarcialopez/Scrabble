package simulatedObjects;

import java.util.List;

import logic.Game;
import logic.WordChecker;

public class EasyStrategy implements Strategy {

	private static final int FORMED_WORDS_LENGTH = 2;
	
	@Override
	public void play(Game game, WordChecker wordChecker, List<Tile> tilesForWord) {
		
		boolean wordWritten = false;
		
		wordWritten = tryWritingInBoard(FORMED_WORDS_LENGTH, tilesForWord, game, wordChecker);
		
		if(!wordWritten) {
			
			// 25% probabilidad de pasar turno.
			if(Game.getRandom().nextDouble() > 0.25) {
				if(!game.swapTile())
					game.passTurn();
			}

			else game.passTurn();
		}
		
	}
}
