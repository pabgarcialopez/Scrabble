package factories;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import gameObjects.Player;
import gameObjects.Tile;

public abstract class PlayerBuilder extends Builder<Player>{

	protected TileBuilder tileBuilder;
	
	public PlayerBuilder(String type, TileBuilder tileBuilder) {
		super(type);
		this.tileBuilder = tileBuilder;
	}

	@Override
	protected Player createTheInstance(JSONObject data) {
		
		if(data.getString("type").equalsIgnoreCase(this._type)) {
			
			String name = data.getString("name");
			int totalPoints = data.getInt("total_points");
			List <Tile> tiles = new ArrayList<Tile>();
			
			if(data.has("tiles")) {
				
				JSONArray jsonArrayTiles = data.getJSONArray("tiles");
				for(int i = 0; i < jsonArrayTiles.length(); i++)
					tiles.add(tileBuilder.createTheInstance(jsonArrayTiles.getJSONObject(i)));
				
			}
			
			return createThePlayer(name, totalPoints, tiles);
		}
		else
			return null;

	}
	
	protected abstract Player createThePlayer(String name, int totalPoints, List<Tile> tiles);
}
