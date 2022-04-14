package simulatedObjects;

import java.util.List;

import logic.Game;
import logic.WordChecker;

public class HardStrategy implements Strategy {
	
	@Override
	public void play(Game game, WordChecker wordChecker, List<Tile> tilesForWord) {
		boolean wordWritten = false;
		
		int extraTile = (Game.getWordsInBoard() ? 1 : 0);
		for(int wordLength = tilesForWord.size() + extraTile; wordLength > 1 && !wordWritten; --wordLength)
			wordWritten = tryWritingInBoard(wordLength, tilesForWord, game, wordChecker);
		
		if(!wordWritten && !game.swapTile()) 
			game.passTurn();
	}

}
