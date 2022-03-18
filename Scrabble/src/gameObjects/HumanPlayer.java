package gameObjects;

import java.util.List;

public class HumanPlayer extends Player{

	public HumanPlayer(String name, int totalPoints, List<Tile> tiles) {
		super(name, totalPoints, tiles);
	}

	@Override
	public void play() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isHuman() {
		return true;
	}
}