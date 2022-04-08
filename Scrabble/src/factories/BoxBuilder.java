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
	
	/* Sobrescritura del método createTheInstance:
	 * 
	 * Este método construye un objeto de la clase Box, es decir,
	 * una casilla del tablero. La casilla tiene la posibilidad de
	 * tener asociados un efecto especial y una letra, las cuales vienen
	 * especificadas en el JSONObject recibido por parámetro.
	 */
	
	@Override
	protected Box createTheInstance(JSONObject data) {
		
		SpecialEffects specialEffect = null;
		
		if(data.has("special_effect"))
			specialEffect = SpecialEffects.valueOf(data.getString("special_effect"));
		
		if(data.has("tile")) {
			Tile tile = tileBuilder.createTheInstance(data.getJSONObject("tile"));
			return new Box(specialEffect, tile);
		}
		
		else return new Box(specialEffect);
	}

}
