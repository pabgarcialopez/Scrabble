package command;

import java.io.FileNotFoundException;

import exceptions.CommandExecuteException;
import exceptions.CommandParseException;
import gameLogic.Game;
import storage.GameSaver;

public class SaveCommand extends Command {

	private static final String NAME = "guardar";

	private static final String DETAILS = "[g]uardar [nombre del fichero]";

	private static final String SHORTCUT = "g";

	private static final String HELP = "guardar una partida en un fichero";
	
	private String file;
	
	public SaveCommand() {
		super(NAME, SHORTCUT, DETAILS, HELP);
	}
	
	@Override
	public boolean execute(Game game) throws CommandExecuteException {
		
		try {
			GameSaver.saveGame(game, this.file);
			System.out.println("La partida ha sido guardada con éxito.");
		}
		catch(FileNotFoundException fnfe) {
			throw new CommandExecuteException("El fichero introducido no es válido", fnfe);
		}
		catch(IllegalArgumentException iae) {
			throw new CommandExecuteException(iae.getMessage(), iae);
		}
		
		return false;
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
