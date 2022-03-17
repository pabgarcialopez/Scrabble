package command;

import java.io.FileNotFoundException;

import exceptions.CommandExecuteException;
import gameLogic.Game;
import storage.GameLoader;

public class LoadCommand extends Command {

	private static final String NAME = "cargar";

	private static final String DETAILS = "[c]argar [nombre del fichero]";

	private static final String SHORTCUT = "c";

	private static final String HELP = "cargar una partida de fichero";
	
	public LoadCommand() {
		super(NAME, SHORTCUT, DETAILS, HELP);
	}
	
	@Override
	public boolean execute(Game game) throws CommandExecuteException {
		
		try {
			GameLoader.loadGame(game);
		}
		catch(FileNotFoundException fnfe) {
			throw new CommandExecuteException("El fichero introducido no es válido", fnfe);
		}
		
		return true;
	}
	
}
