package factories;

import java.util.List;

import gameObjects.MediumPlayer;
import gameObjects.Player;
import gameObjects.Tile;

public class MediumPlayerBuilder extends PlayerBuilder {

	public MediumPlayerBuilder(TileBuilder tileBuilder) {
		super("medium_player", tileBuilder);
	}

	@Override
	protected Player createThePlayer(String name, int totalPoints, List<Tile> tiles) {
		
		return new MediumPlayer(name, totalPoints, tiles);
	}
}
