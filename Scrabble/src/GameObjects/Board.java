package GameObjects;

import java.util.ArrayList;
import java.util.List;

public class Board {
	
	private List<List<Box>> board;
	
	Board() {
		
		this.board = new ArrayList<List<Box>>();
		
	}
	
	public int getBoardSize() {
		return this.board.size();
	}
	
	public int getNumBoxes() {
		int numBoxes = this.getBoardSize();
		return numBoxes*numBoxes;
		
	}
	

}
