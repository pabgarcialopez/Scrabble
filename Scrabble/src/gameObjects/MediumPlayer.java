package gameObjects;

import java.util.List;

public class MediumPlayer extends Player {
	
	private static int numMediumPlayers = 0;

	public MediumPlayer(String name, int totalPoints, List<Tile> tiles) {
		super(name + " Medium " + ++numMediumPlayers, totalPoints, tiles);
	}

	@Override
	public void play() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isHuman() {
		return false;
	}

}
