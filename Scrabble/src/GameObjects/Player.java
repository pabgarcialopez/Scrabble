package GameObjects;

import java.util.ArrayList;
import java.util.List;

import GameUtils.StringUtils;

public class Player {
	
	private String name;
	private List<Tile> tiles;
	private int totalPoints;
	
	public Player(String name) {
		this.name = name;
		this.totalPoints = 0;
		this.tiles = new ArrayList<Tile>();
	}

	public String getName() {
		return name;
	}
	
	public void addTile(Tile tile) {
		this.tiles.add(tile);
	}
	
	public int getNumTiles() {
		return tiles.size();
	}

	public String getStatus() {
		StringBuilder buffer = new StringBuilder();
		
		// Nombre del jugador
		buffer.append("Turno de ").append(this.name).append(":")
			  .append(StringUtils.LINE_SEPARATOR);
		
		// Puntos del jugador
		buffer.append("Puntos totales: ").append(totalPoints).append(StringUtils.LINE_SEPARATOR);
	
		// Fichas del jugador
		buffer.append("Fichas (letra y puntos asociados a ella):").append(StringUtils.LINE_SEPARATOR);
		for(int i = 0; i < tiles.size(); i++) {
			buffer.append(tiles.get(i));
			if(i != tiles.size() - 1)
				buffer.append(" || ");
		}
		
		buffer.append(StringUtils.LINE_SEPARATOR);
		
		
		
		return buffer.toString();
	}

}
