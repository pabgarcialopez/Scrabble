package simulatedObjects;

import java.util.List;

import logic.Game;

public class EasyStrategy implements Strategy {

	private static final int FORMED_WORDS_LENGTH = 2;
	
	@Override
	public void play(Game game) {
		
		boolean wordWritten = false;
		
		Player currentPlayer = game.getPlayers().getPlayer(game.getCurrentTurn());
		List<Tile> tilesForWord = currentPlayer.getTiles();
		
		wordWritten = currentPlayer.tryWritingInBoard(FORMED_WORDS_LENGTH, tilesForWord, game);
		
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
