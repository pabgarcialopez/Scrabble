package factories;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import gameObjects.Player;
import gameObjects.Tile;

public class PlayerBuilder extends Builder<Player>{

	private TileBuilder tileBuilder;
	
	public PlayerBuilder(TileBuilder tileBuilder) {
		super("player");
		this.tileBuilder = tileBuilder;
	}

	@Override
	protected Player createTheInstance(JSONObject data) {
		
		String name = data.getString("name");
		int totalPoints = data.getInt("total_points");
		List <Tile> tiles = new ArrayList<Tile>();
		
		if(data.has("tiles")) {
			
			JSONArray jsonArrayTiles = data.getJSONArray("tiles");
			for(int i = 0; i < jsonArrayTiles.length(); i++)
				tiles.add(tileBuilder.createTheInstance(jsonArrayTiles.getJSONObject(i)));
			
		}
		
		return new Player(name, totalPoints, tiles);
	}

}
