package gameObjects;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import gameLogic.Game;

public class HardPlayer extends Player{
	
	private static int numHardPlayers = 0;

	public HardPlayer(String name, int totalPoints, List<Tile> tiles) {
		super(name + " Hard " + ++numHardPlayers, totalPoints, tiles);
	}

	@Override
	public void play(Game game) {
		
		boolean played = false;
		
		List<Tile> tilesForWord = new ArrayList<Tile>(this.tiles);
		
		if(!game.getWordsInBoard()) {
			for(int wordLength = tilesForWord.size(); wordLength > 1 && !played; --wordLength)
				played = tryWritingInEmptyBoard(wordLength, tilesForWord, game);
		}
		else {
			for(int wordLength = tilesForWord.size() + 1; wordLength > 1 && !played; --wordLength)
				played = tryWritingInNotEmptyBoard(wordLength, tilesForWord, game);
		}
		
		
		if(!played && !game.swapTile()) {
			game.passTurn();
		}
	}

	@Override
	public boolean isHuman() {
		return false;
	}
	
	@Override
	public void reset() {
		--numHardPlayers;
	}
	
	@Override
	public JSONObject report() {
		
		JSONObject jo = super.report();
		jo.put("type", "hard_player");
		jo.put("name", name.substring(0, name.lastIndexOf(" Hard")));
		
		return jo;
	}
	
	

}
