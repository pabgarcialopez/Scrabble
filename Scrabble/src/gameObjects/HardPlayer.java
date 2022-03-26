package gameObjects;

import java.util.List;

import gameLogic.Game;

public class HardPlayer extends Player{
	
	private static int numHardPlayers = 0;

	public HardPlayer(String name, int totalPoints, List<Tile> tiles) {
		super(name + " Hard " + ++numHardPlayers, totalPoints, tiles);
	}

	@Override
	public void play(Game game) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isHuman() {
		return false;
	}

}
