package GameObjects;

import java.util.ArrayList;
import java.util.List;

public class Board {
	
	private static final int boardSize = 15; // Lado del tablero.
	
	private List<List<Box>> board;
	
	Board() {
		this.board = new ArrayList<List<Box>>(boardSize);
	}
	
	public int getBoardSize() {
		return this.board.size();
	}
	
	public int getNumBoxes() {
		int numBoxes = this.getBoardSize();
		return numBoxes*numBoxes;
	}
	

}
