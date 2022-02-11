package GameContainers;
import java.util.ArrayList;
import java.util.List;

import GameObjects.Tile;

public class GameTiles {

	private List<Tile> tiles;
	
	GameTiles() {
		this.tiles = new ArrayList<Tile>();
	}
	
	private void add(Tile tile) {
		this.tiles.add(tile);
	}
	
	private void remove(Tile tile) {
		this.tiles.remove(tile);
	}
}
