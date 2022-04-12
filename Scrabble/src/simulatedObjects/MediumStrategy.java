package simulatedObjects;

import java.util.ArrayList;
import java.util.List;

import logic.Game;

public class MediumStrategy implements Strategy {

	@Override
	public void play(Game game) {
		boolean wordWritten = false;
		
		Player currentPlayer = game.getPlayers().getPlayer(game.getCurrentTurn());
		List<Tile> tilesForWord = currentPlayer.getTiles();
		
		List<Integer> lengths = new ArrayList<Integer>();
		for(int i = 2; i <= tilesForWord.size(); ++i)
			lengths.add(i);
		
		if(Game.getWordsInBoard())
			lengths.add(tilesForWord.size() + 1);

		while(!wordWritten && lengths.size() > 0) {
			int i = (int) (Game.getRandom().nextDouble() * lengths.size());
			wordWritten = currentPlayer.tryWritingInBoard(lengths.remove(i), tilesForWord, game);
		}
		
		if(!wordWritten) {
			
			if(Game.getRandom().nextDouble() > 0.5) {
				if(!game.swapTile())
					game.passTurn();
			}
			
			else game.passTurn();
		}
	}

}
