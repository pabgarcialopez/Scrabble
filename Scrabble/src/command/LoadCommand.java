package command;

import java.io.FileNotFoundException;

import org.json.JSONException;

import exceptions.CommandExecuteException;
import exceptions.CommandParseException;
import gameLogic.Game;
import gameUtils.StringUtils;
import storage.GameLoader;

public class LoadCommand extends Command {

	private static final String NAME = "cargar";

	private static final String DETAILS = "[c]argar [nombre del fichero]";

	private static final String SHORTCUT = "c";

	private static final String HELP = "cargar una partida de fichero";
	
	private String file;
	
	public LoadCommand() {
		super(NAME, SHORTCUT, DETAILS, HELP);
	}
	
	@Override
	public boolean execute(Game game) throws CommandExecuteException {
		
		try {
			GameLoader.loadGame(game, this.file);
		}
		catch(FileNotFoundException fnfe) {
			throw new CommandExecuteException("El fichero introducido no se ha podido encontrar." + StringUtils.LINE_SEPARATOR, fnfe);
		}
		catch(JSONException je) {
			throw new CommandExecuteException("El formato JSON del fichero introducido no es válido." + StringUtils.LINE_SEPARATOR, je);
		}
		
		return true;
	}
	
	@Override
	protected Command parse(String[] words) throws CommandParseException {
		
		if (!matchCommandName(words[0])) return null;
		
		if (words.length != 2)
			throw new CommandParseException(String.format("[ERROR]: Comando %s: %s%n", words[0] ,INCORRECT_NUMBER_OF_ARGS_MSG));

		this.file = words[1] + ".json";
		
		return this;
	}
}
