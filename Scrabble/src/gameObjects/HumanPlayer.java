package gameObjects;

import java.util.List;

import org.json.JSONObject;

import gameLogic.Game;

public class HumanPlayer extends Player{

	public HumanPlayer(String name, int totalPoints, List<Tile> tiles) {
		super(name, totalPoints, tiles);
	}

	@Override
	public void play(Game game) {}

	@Override
	public boolean isHuman() {
		return true;
	}
	
	@Override
	public JSONObject report() {
		
		JSONObject jo = super.report();
		jo.put("type", "human_player");
		jo.put("name", this.name);
		
		return jo;
	}

	@Override
	public void reset() {}
}
