package gameObjects;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import gameLogic.Game;

public class EasyPlayer extends Player {

	private static int numEasyPlayers = 0;
	
	private static final int FORMED_WORDS_LENGTH = 2;
	
	
	public EasyPlayer(String name, int totalPoints, List<Tile> tiles) {
		super(name + " Easy " + ++numEasyPlayers, totalPoints, tiles);
	}

	@Override
	public void play(Game game) {
		
		boolean played = false;
		
		List<Tile> tilesForWord = new ArrayList<Tile>(this.tiles);
		
		if(!game.getWordsInBoard())
			played = tryWritingInEmptyBoard(FORMED_WORDS_LENGTH, tilesForWord, game);
		
		else played = tryWritingInNotEmptyBoard(FORMED_WORDS_LENGTH, tilesForWord, game);
		
		if(!played && !game.swapTile()) {
			//if((int) (this.rdm.nextDouble() * 4) == 0) // 25% de probabilidad de pasar.
				game.passTurn();
		}		
	}

	@Override
	public boolean isHuman() {
		return false;
	}
	
	@Override
	public JSONObject report() {
		
		JSONObject jo = super.report();
		jo.put("type", "easy_player");
		jo.put("name", name.substring(0, name.lastIndexOf(" Easy")));
		
		return jo;
	}

	@Override
	public void reset() {
		--numEasyPlayers;
	}
}
