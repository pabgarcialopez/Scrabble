package factories;

import java.util.List;

import gameObjects.HumanPlayer;
import gameObjects.Player;
import gameObjects.Tile;

// Ver apuntes de la clase padre PlayerBuilder
public class HumanPlayerBuilder extends PlayerBuilder {

	public HumanPlayerBuilder(TileBuilder tileBuilder) {
		super("human_player", tileBuilder);
	}

	@Override
	protected Player createThePlayer(String name, int totalPoints, List<Tile> tiles) {
		return new HumanPlayer(name, totalPoints, tiles);
	}
}
