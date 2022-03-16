package factories;

import org.json.JSONObject;

import gameObjects.Tile;

public class TileBuilder extends Builder<Tile>{

	public TileBuilder() {
		super("tile");
	}

	@Override
	protected Tile createTheInstance(JSONObject data) {
		
		String letter = data.getString("letter");
		int points = data.getInt("points");
		
		return new Tile(letter, points);
	}

}
