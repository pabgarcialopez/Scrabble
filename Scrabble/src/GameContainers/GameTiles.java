package GameContainers;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import GameObjects.Tile;

public class GameTiles {

	private List<Tile> tiles;
	
	public GameTiles() {
		this.tiles = new ArrayList<Tile>();
	}
	
	private void add(Tile tile) {
		this.tiles.add(tile);
	}
	
	private void remove(Tile tile) {
		this.tiles.remove(tile);
	}
	
	public void loadTiles(String file){
		
		try(BufferedReader buffer = new BufferedReader(new FileReader(file))) {
			String linea = null;
			while((linea = buffer.readLine()) != null) {
				String[] tile = linea.trim().split(" ");
				for(int i = 0; i < Integer.parseInt(tile[1]); ++i)
					this.add(new Tile(tile[0], Integer.parseInt(tile[2])));
			}
			
		}
		catch (IOException ioe) {
			throw new IllegalArgumentException("The file of tiles is not valid", ioe);
		}
		catch (NumberFormatException nfe) {
			throw new IllegalArgumentException("The file of tiles is not valid", nfe);
		}
	}
	
	public String getLetter(int i) {
		return this.tiles.get(i).getLetter();
	}
	
	public int getNumTiles() {
		return this.tiles.size();
	}
}
