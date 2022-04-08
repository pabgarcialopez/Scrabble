package factories;

import java.util.List;

import gameObjects.HardPlayer;
import gameObjects.Player;
import gameObjects.Tile;

// Ver apuntes de la clase padre PlayerBuilder
public class HardPlayerBuilder extends PlayerBuilder {
	
	public HardPlayerBuilder(TileBuilder tileBuilder) {
		super("hard_player", tileBuilder);
	}

	@Override
	protected Player createThePlayer(String name, int totalPoints, List<Tile> tiles) {
		return new HardPlayer(name, totalPoints, tiles);
	}
}
