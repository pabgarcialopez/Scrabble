package factories;

import org.json.JSONObject;

import simulatedObjects.Tile;

public class TileBuilder extends Builder<Tile>{

	public TileBuilder() {
		super("tile");
	}

	/* Sobrescritura del método createTheInstance:
	 * 
	 * Construye y devuelve una instancia de la clase Tile,
	 * obteniendo la información necesaria del JSONObject recibido por parámetro.
	 */
	@Override
	protected Tile createTheInstance(JSONObject data) {
		
		String letter = data.getString("letter");
		int points = data.getInt("points");
		
		return new Tile(letter, points);
	}

}
