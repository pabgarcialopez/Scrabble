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
	
	public void remove(Tile tile) {
		this.tiles.remove(tile);
	}
	
	public void loadTiles(String file){
		
		try(BufferedReader buffer = new BufferedReader(new FileReader(file))) {
			String linea = null;
			
			while((linea = buffer.readLine()) != null) {
				String[] tile = linea.trim().split(" ");
				// tile[0] es la letra; tile[1] es el numero fichas con esa letra; tile[2] son los puntos de esa letra.
				for(int i = 0; i < Integer.parseInt(tile[1]); ++i)
					this.add(new Tile(tile[0], Integer.parseInt(tile[2])));
			}
			
		}
		catch (IOException ioe) {
			throw new IllegalArgumentException("El fichero de casillas no es valido", ioe);
		}
		catch (NumberFormatException nfe) {
			throw new IllegalArgumentException("El fichero de casillas no es valido", nfe);
		}
	}
	
	
	public int getNumTiles() {
		return this.tiles.size();
	}

	public Tile getTile(int i) {
		 
		return tiles.get(i);
	}
}
