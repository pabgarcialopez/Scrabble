package simulatedObjects;

import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import strategies.Strategy;

/* APUNTES GENERALES:
   
   La clase Player es una clase abstracta que encapsula el modelo de un jugador.
   
   Sus atributos son:
   
   - Nombre: así será identificado el jugador.
   - Lista de fichas: son las fichas con las que el jugador puede formar palabras.
   - Puntos totales: acumulación de puntos obtenidos de colocar palabras.
   - Objeto de la clase Random: se emplea para establecer una posición de palabra
     aleatoria en los jugadores automáticos.
     
   El atributo final y constante "movingBoxes" es empleado por los jugadores automáticos
   para recorrer las posiciones del tablero de forma ordenada.
   
 */
public class Player {
	
	protected String name;
	protected List<Tile> tiles;
	private int totalPoints;
	private Strategy strategy;
	
	public Player(String name, int totalPoints, List<Tile> tiles, Strategy strategy) {
		this.name = name;
		this.tiles = tiles;
		this.totalPoints = totalPoints;
		this.strategy = strategy;
	}
	
	// Getters
	
	public String getName() {
		return name;
	}
	
	public int getNumTiles() {
		return tiles.size();
	}
	
	public Tile getTile(int tile) {
		return this.tiles.get(tile);
	}
	
	public Tile getTile(String letter) {
		
		for(int i = 0; i < this.tiles.size(); ++i)
			if(this.tiles.get(i).getLetter().equalsIgnoreCase(letter))
				return this.tiles.get(i);
		
		return null;
	}

	public int getPoints() {
		return this.totalPoints;
	}

	public List<Tile> getTiles() {
		return Collections.unmodifiableList(this.tiles);
	}
	
	public JSONObject report() {
		
		JSONObject jo = new JSONObject();
		
		jo.put("name", name.substring(0, name.indexOf(" (")));
		
		jo.put("total_points", this.totalPoints);
		
		JSONArray tiles = new JSONArray();
		for(int i = 0; i < this.tiles.size(); ++i)
			tiles.put(this.tiles.get(i).report());
		
		jo.put("tiles", tiles);
		
		JSONObject strategy = new JSONObject();
		strategy.put("strategy_type", this.strategy.toString());
		
		jo.put("strategy", strategy);
		
		return jo;
	}

	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}
}
