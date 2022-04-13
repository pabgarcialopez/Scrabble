package factories;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import simulatedObjects.Player;
import simulatedObjects.Strategy;
import simulatedObjects.Tile;

/* APUNTES GENERALES
   
   Ver apuntes de la clase padre Builder.
   
   La clase PlayerBuilder es abstracta, dado que se puede
   reutilizar esta misma clase para construir los jugadores
   clasificados por dificultad.
   
 */
public class PlayerBuilder extends Builder<Player>{

	protected TileBuilder tileBuilder;
	private List<StrategyBuilder> strategyBuilders;
	
	public PlayerBuilder(TileBuilder tileBuilder, List<StrategyBuilder> strategyBuilders) {
		super("player");
		this.tileBuilder = tileBuilder;
		this.strategyBuilders = strategyBuilders;
	}
	
	/* Sobrescritura del método createTheInstance:
	 * 
	 * El método construye un objecto polimórfico de tipo estático Player,
	 * y de tipo dinámico HumanPlayer, EasyPlayer, MediumPlayer o HardPlayer,
	 * y lo devuelve.
	 * 
	 * En el JSONObject recibido por parámetro, se pueden dar algunas alternativas:
	 * 
	 * - Si el tipo del JSONObject no coincide con el tipo del jugador actual, se devuelve null
	 *   (esto se usa en la clase GamePlayersBuilder para distinguir cuando el fichero .json
	 *   podría tener un error).
	 *   
	 * - Si el jugador tiene una clave "name", se asocia el nombre del jugador a este.
	 *   En caso contrario, se trata de un jugador automático sin nombre, y se le asigna "CPU".
	 *   
	 * - Si el jugador tiene fichas asociadas, se crea el correspondiente array de objetos Tile.
	 */

	@Override
	protected Player createTheInstance(JSONObject data) {
		
		String name;
		if(data.has("name"))
			name = data.getString("name");
		else name = "CPU";
		
		int totalPoints = data.getInt("total_points");
		List <Tile> tiles = new ArrayList<Tile>();
		
		if(data.has("tiles")) {
			
			JSONArray jsonArrayTiles = data.getJSONArray("tiles");
			for(int i = 0; i < jsonArrayTiles.length(); i++)
				tiles.add(tileBuilder.createTheInstance(jsonArrayTiles.getJSONObject(i)));
			
		}
		
		Strategy strategy = null;
		for(StrategyBuilder sb : strategyBuilders) {
			strategy = sb.createTheInstance(data.getJSONObject("strategy"));
			
			if(strategy != null)
				break;
		}
		
		if(strategy == null)
			throw new InputMismatchException("El JSON no es válido (strategy).");
		
		return new Player(name, totalPoints, tiles, strategy);
	}
}
