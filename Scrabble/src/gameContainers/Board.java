package gameContainers;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import gameObjects.Box;
import gameObjects.Tile;
import gameUtils.Pair;

public class Board {
	
	private List<List<Box>> board;
	private Pair<Integer, Integer> center;
	
	//public Board() {
	//	this.board = new ArrayList<List<Box>>();
	//}
	
	// Constructor para nuevas y anteriores partidas.
	public Board(List<List<Box>> board) {
		this.board = board;
		this.center = searchCenter();
	}
	
	private Pair<Integer, Integer> searchCenter() {
		for(int i = 0; i < board.size(); i++) {
			for(int j = 0; j < board.size(); j++) {
				if(board.get(i).get(j).isCentre())
					return new Pair<Integer, Integer>(i, j);
			}
		}
		
		return null;
	}

	public void add(List<Box> row) {
		board.add(row);
	}
	
	public int getBoardSize() {
		return this.board.size();
	}
	
	public int getNumBoxes() {
		int numBoxes = this.getBoardSize();
		return numBoxes*numBoxes;
	}

	public Tile getTile(int posX, int posY) {
		
		return board.get(posX).get(posY).getTile();
	}

	public void assignTile(Tile tile, int posX, int posY) {

		board.get(posX).get(posY).assignTile(tile);
	}

	public int getPoints(int posX, int posY) {
		
		return board.get(posX).get(posY).getPoints();
	}

	public boolean isCentre(int posX, int posY) {
		return board.get(posX).get(posY).isCentre();
	}

	public Box getBoxAt(int posX, int posY) {
		return board.get(posX).get(posY);
	}

	public int getWordMultiplier(int posX, int posY) {
		
		return this.board.get(posX).get(posY).getWordMultiplier();
	}
	
	public Pair<Integer, Integer> getCenter() {
		return center;
	}
	
	public JSONObject report() {
		
		JSONObject jo = new JSONObject();
		JSONArray board = new JSONArray();
		
		for(int i = 0; i < this.board.size(); ++i) {
			JSONArray row = new JSONArray();
			
			for(int j = 0; j < this.board.get(i).size(); ++j) 
				row.put(this.board.get(i).get(j).report());
			
			board.put(row);
		}
		
		jo.put("board", board);
		
		return jo;
	}
}
