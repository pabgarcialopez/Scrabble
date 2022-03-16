package factories;

import org.json.JSONObject;

import gameObjects.Box;
import gameObjects.SpecialEffects;
import gameObjects.Tile;

public class BoxBuilder extends Builder<Box>{
	
	private TileBuilder tileBuilder;

	public BoxBuilder(TileBuilder tileBuilder) {
		super("Box");
		this.tileBuilder = tileBuilder;
	}
	
	@Override
	protected Box createTheInstance(JSONObject data) {
		
		SpecialEffects specialEffect = SpecialEffects.valueOf(data.getString("special_effect"));
		
		if(data.has("tile")) {
			Tile tile = tileBuilder.createTheInstance(data.getJSONObject("tile"));
			return new Box(specialEffect, tile);
		}
		
		else return new Box(specialEffect);
	}

}
