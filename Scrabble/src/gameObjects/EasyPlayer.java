package gameObjects;

import java.util.List;

import exceptions.CommandExecuteException;
import gameLogic.Game;
import gameUtils.Pair;

public class EasyPlayer extends Player {

	private static int numEasyPlayers = 0;
	
	public EasyPlayer(String name, int totalPoints, List<Tile> tiles) {
		super(name + " Easy " + ++numEasyPlayers, totalPoints, tiles);
	}

	@Override
	public void play(Game game) {
		
		boolean played = false;
		
		if(game.getWordsInBoard()) {
			
			for(int tileNumber = 0; tileNumber < this.getNumTiles() && !played; ++tileNumber)
				for(int i = 0; i < game.getBoardSize() && !played; ++i)
					for(int j = 0; j < game.getBoardSize() && !played; ++j)
						played = tryWritingAWord(i, j, this.getTile(tileNumber), game);
		}
		
		if(!played && !game.swapTile())
			game.passTurn();
	}

	@Override
	public boolean isHuman() {
		return false;
	}
	
	private boolean tryWritingAWord(int posX, int posY, Tile tile, Game game) {
		
		Box box = game.getBoxAt(posX, posY);
		
		if(box.getTile() != null) {
			
			for(Pair<Integer, Integer> move : movingBoxes) {
				
				int newPosX = posX, newPosY = posY;
				
				String word = "", direction;
				
				word += box.getTile().getLetter();
				
				if(move.getFirst().equals(-1) || move.getSecond().equals(-1)) {
					word = tile.getLetter() + word;
					newPosX += move.getFirst();
					newPosY += move.getSecond();
				}
				else {
					word += tile.getLetter();
				}
				
				
				if(move.getFirst().equals(0))
					direction = "H";
				else 
					direction = "V";
				
				try {
					game.checkArguments(word, newPosX, newPosY, direction);
					game.writeAWord(word, newPosX, newPosY, direction);
					return true;
				}
				catch(CommandExecuteException iae) {}
			}
			
		}
		
		return false;
	}

}
