package storage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONObject;

import gameContainers.Board;
import gameContainers.GamePlayers;
import gameContainers.GameTiles;
import gameObjects.Box;
import gameObjects.SpecialEffects;
import gameObjects.Tile;

public class GameLoader {
	
	private GamePlayers players;
	
	private static final String tilesFile = "tiles.txt";
	private static final String boxesFile = "boxes.txt";
	private static final String wordsFile = "words.txt";
	
	private int currentTurn;
	private int numConsecutivePassedTurns;
	private int numTurnsWithoutTiles;
	
	private boolean wordsInBoard;
	private boolean gameFinished;
	
	private GameTiles tiles;
	private Board board;
	private List<String> words;
	private List<String> usedWords;

	public GameLoader() {
		
	}
	
	public void loadGame() {
		JSONObject jo = new JSONObject();
		
		
	}
	
	private void loadTiles(String file){
		
		try(BufferedReader buffer = new BufferedReader(new FileReader(file))) {
			String linea = null;
			
			while((linea = buffer.readLine()) != null) {
				String[] tile = linea.trim().split(" ");
				// tile[0] es la letra; tile[1] es el numero fichas con esa letra; tile[2] son los puntos de esa letra.
				for(int i = 0; i < Integer.parseInt(tile[1]); ++i)
					tiles.add(new Tile(tile[0], Integer.parseInt(tile[2])));
			}
			
		}
		catch (IOException ioe) {
			throw new IllegalArgumentException("Error al leer el fichero tiles.txt", ioe);
		}
		catch (NumberFormatException nfe) {
			throw new IllegalArgumentException("Error al leer el fichero tiles.txt", nfe);
		}
	}
	
	private void loadBoard(String file) {
		try(BufferedReader buffer = new BufferedReader(new FileReader(file))) {
			
			int xSize, ySize;
			String line = buffer.readLine();
			String[] size = line.trim().split(" ");
			xSize = Integer.parseInt(size[0]);
			ySize = Integer.parseInt(size[1]);
			for (int i = 0; i < xSize; ++i) {
				List<Box> row = new ArrayList<Box>();
				for(int j = 0; j < ySize; ++j) {
					line = buffer.readLine();
					line = line.trim();
					row.add(new Box(SpecialEffects.valueOf(line)));
				}
				this.board.add(row);
			}					
		}
		catch (IOException ioe) {
			throw new IllegalArgumentException("Error al leer el archivo board.txt", ioe);
		}
		catch (NumberFormatException nfe) {
			throw new IllegalArgumentException("Error al leer el archivo board.txt", nfe);
		}
	}

	private void loadWordList(String file) {
		
		try(BufferedReader buffer = new BufferedReader(new FileReader(file))) {
			String linea = null;
			while((linea = buffer.readLine()) != null) {
				linea = linea.trim();
				this.words.add(linea);
			}
		}
		catch (IOException ioe) {
			throw new IllegalArgumentException("Error al leer el fichero words.txt", ioe);
		}
	}
	
	public Board getBoard() {
		return this.board;
	}
	
	
}
