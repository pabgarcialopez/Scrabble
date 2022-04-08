package factories;

import java.util.List;

import gameObjects.EasyPlayer;
import gameObjects.Player;
import gameObjects.Tile;

// Ver apuntes de la clase padre PlayerBuilder
public class EasyPlayerBuilder extends PlayerBuilder {

	public EasyPlayerBuilder(TileBuilder tileBuilder) {
		super("easy_player", tileBuilder);
	}

	@Override
	protected Player createThePlayer(String name, int totalPoints, List<Tile> tiles) {
		return new EasyPlayer(name, totalPoints, tiles);
	}
}
