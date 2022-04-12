package simulatedObjects;

import java.util.List;

import logic.Game;

public class HardStrategy implements Strategy {
	
	@Override
	public void play(Game game) {
		boolean wordWritten = false;
		
		Player currentPlayer = game.getPlayers().getPlayer(game.getCurrentTurn());
		List<Tile> tilesForWord = currentPlayer.getTiles();
		
		int extraTile = (Game.getWordsInBoard() ? 1 : 0);
		for(int wordLength = tilesForWord.size() + extraTile; wordLength > 1 && !wordWritten; --wordLength)
			wordWritten = currentPlayer.tryWritingInBoard(wordLength, tilesForWord, game);
		
		if(!wordWritten && !game.swapTile()) 
			game.passTurn();
	}

}
