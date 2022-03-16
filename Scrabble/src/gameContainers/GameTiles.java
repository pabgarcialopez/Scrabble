package gameContainers;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import gameObjects.Tile;

public class GameTiles {

	private List<Tile> tiles;
	
	public GameTiles() {
		this.tiles = new ArrayList<Tile>();
	}
	
	// Constructor para la carga de partida
	public GameTiles(List<Tile> tiles) {
		this.tiles = tiles;
	}

	public void add(Tile tile) {
		tiles.add(tile);
	}
	
	public void remove(Tile tile) {
		tiles.remove(tile);
	}
	
	public int getNumTiles() {
		return tiles.size();
	}

	public Tile getTile(int i) {
		return tiles.get(i);
	}

	public int getSize() {
		return tiles.size();
	}
}
