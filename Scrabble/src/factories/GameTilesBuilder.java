package factories;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import gameContainers.GameTiles;
import gameObjects.Tile;

public class GameTilesBuilder extends Builder<GameTiles>{

	private TileBuilder tileBuilder;
	GameTilesBuilder(TileBuilder tileBuilder) {
		super("gameTiles");
		this.tileBuilder = tileBuilder;
	}

	@Override
	protected GameTiles createTheInstance(JSONObject data) {
		
		if(data == null)
			return new GameTiles();
		
		else {
			JSONArray jsonArrayTiles = data.getJSONArray("tiles");
			List<Tile> tiles = new ArrayList<Tile>();
			
			for(int i = 0; i < jsonArrayTiles.length(); i++)
				tiles.add(tileBuilder.createTheInstance(jsonArrayTiles.getJSONObject(i)));
			
			return new GameTiles(tiles);
				
			
		}
	}

}
