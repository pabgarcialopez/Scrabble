package gameObjects;

import java.util.ArrayList;
import java.util.List;

import gameUtils.StringUtils;

public class Player {
	
	private String name;
	private List<Tile> tiles;
	private int totalPoints;
	
	// Constructor para cuando se carga partida
	public Player(String name, int totalPoints, List<Tile> tiles) {
		
	}
	
	// Constructor para cuando se crea nueva partida
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

	public Tile getTile(int tile) {
		
		return this.tiles.get(tile);
	}

	public void removeTile(int tile) {

		this.tiles.remove(tile);		
	}

	public boolean hasLetter(String letter) {
		
		for(int i = 0; i < this.tiles.size(); ++i)
			if(this.tiles.get(i).getLetter().equalsIgnoreCase(letter))
				return true;
		
		return false;
	}

	public Tile getTile(String letter) {
		
		for(int i = 0; i < this.tiles.size(); ++i)
			if(this.tiles.get(i).getLetter().equalsIgnoreCase(letter))
				return this.tiles.get(i);
		
		return null;
	}

	public void removeTile(Tile tile) {

		this.tiles.remove(tile);
	}

	public int numberOfTilesOf(String letter) {
		
		int numberOfTiles = 0;
		for (int i = 0; i < this.tiles.size(); ++i)
			if (this.tiles.get(i).getLetter().equalsIgnoreCase(letter)) 
				++numberOfTiles;
		
		return numberOfTiles;
	}

	public void givePoints(int points) {
		this.totalPoints += points;
	}

}
