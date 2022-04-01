package command;

import java.io.FileNotFoundException;

import exceptions.CommandExecuteException;
import exceptions.CommandParseException;
import gameUtils.StringUtils;
import scrabble.Controller;

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
	public boolean execute(Controller controller) throws CommandExecuteException {
		
		try {
			controller.saveGame(this.file);
			System.out.println(StringUtils.LINE_SEPARATOR + "La partida ha sido guardada con exito.");
		}
		catch(FileNotFoundException fnfe) {
			throw new CommandExecuteException("El fichero introducido no es valido", fnfe);
		}
		catch(IllegalArgumentException iae) {
			throw new CommandExecuteException(iae.getMessage(), iae);
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
