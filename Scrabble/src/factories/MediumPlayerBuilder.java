package factories;

import java.util.List;

import simulatedObjects.MediumPlayer;
import simulatedObjects.Player;
import simulatedObjects.Tile;

// Ver apuntes de la clase padre PlayerBuilder
public class MediumPlayerBuilder extends PlayerBuilder {

	public MediumPlayerBuilder(TileBuilder tileBuilder) {
		super("medium_player", tileBuilder);
	}

	@Override
	protected Player createThePlayer(String name, int totalPoints, List<Tile> tiles) {
		return new MediumPlayer(name, totalPoints, tiles);
	}
}
