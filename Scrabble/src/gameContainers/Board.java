package gameContainers;

import java.util.List;

import gameObjects.Box;
import gameObjects.Tile;

public class Board {
	
	private List<List<Box>> board;
	
	//public Board() {
	//	this.board = new ArrayList<List<Box>>();
	//}
	
	// Constructor para nuevas y anteriores partidas.
	public Board(List<List<Box>> board) {
		this.board = board;
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
}
