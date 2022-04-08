package gameObjects;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import gameLogic.Game;

public class MediumPlayer extends Player {
	
	private static int numMediumPlayers = 0;

	public MediumPlayer(String name, int totalPoints, List<Tile> tiles) {
		super(name + " Medium " + ++numMediumPlayers, totalPoints, tiles);
	}

	@Override
	public void play(Game game) {
		
		boolean played = false;
		
		List<Integer> lengths = new ArrayList<Integer>();
		for(int i = 2; i <= this.getNumTiles(); ++i)
			lengths.add(i);
		
		List<Tile> tilesForWord = new ArrayList<Tile>(this.tiles);
		
		if(!game.getWordsInBoard()) {
			while(!played && lengths.size() > 0) {
				int i = (int) (this.rdm.nextDouble() * lengths.size());
				played = this.tryWritingInEmptyBoard(lengths.remove(i), tilesForWord, game);
			}
		}
		else {
			lengths.add(this.getNumTiles() + 1);
			while(!played && lengths.size() > 0) {
				int i = (int) (this.rdm.nextDouble() * lengths.size());
				played = this.tryWritingInNotEmptyBoard(lengths.remove(i), tilesForWord, game);
			}
		}
		
		if(!played) {
			int i = (int) (this.rdm.nextDouble() * 2);
			if(i == 0 && !game.swapTile())
				game.passTurn();
		}
	}

	@Override
	public boolean isHuman() {
		return false;
	}
	
	@Override
	public void reset() {
		--numMediumPlayers;
	}
	
	@Override
	public JSONObject report() {
		
		JSONObject jo = super.report();
		jo.put("type", "medium_player");
		
		jo.put("name", name.substring(0, name.lastIndexOf(" Medium")));
		
		return jo;
	}
}
