package storage;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import gameLogic.Game;

public class GameSaver {

	public static void saveGame(Game game, String file) throws FileNotFoundException {
		
		if (file.equals(GameLoader.NEW_GAME))
			throw new IllegalArgumentException("El fichero donde guardar la partida no puede ser el fichero de reseteo.");
		
		OutputStream out = new FileOutputStream("partidas/" + file);
		
		@SuppressWarnings("resource")
		PrintStream p = new PrintStream(out);
		
		p.print(game.report());
	}
}
