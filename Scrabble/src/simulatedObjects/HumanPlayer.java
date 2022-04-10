package simulatedObjects;

import java.util.List;

import org.json.JSONObject;

import logic.Game;

// Ver apuntes de la clase padre Player.
public class HumanPlayer extends Player {

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
	public void reset() {}
	
	@Override
	public JSONObject report() {
		
		JSONObject jo = super.report();
		jo.put("type", "human_player");
		jo.put("name", this.name);
		
		return jo;
	}
}
