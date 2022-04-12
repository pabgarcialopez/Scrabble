package command;

import java.io.FileNotFoundException;

import org.json.JSONException;

import exceptions.CommandExecuteException;
import exceptions.CommandParseException;
import scrabble.Controller;

// Ver apuntes de la clase padre Command.
public class LoadCommand extends Command {

	private static final String NAME = "cargar";

	private static final String DETAILS = "[c]argar [nombre del fichero]";

	private static final String SHORTCUT = "c";

	private static final String HELP = "cargar una partida de fichero";
	
	private String file;
	
	public LoadCommand() {
		super(NAME, SHORTCUT, DETAILS, HELP);
	}
	
	/* Sobrescritura del método execute:
	 * Delega en la clase Controller la carga del juego.
	 */
	
	@Override
	public boolean execute(Controller controller) throws CommandExecuteException {
		
		try {
			controller.loadGame(this.file);
		}
		
		catch(FileNotFoundException fnfe) {
			throw new CommandExecuteException("El fichero introducido no se ha podido encontrar.", fnfe);
		}
		
		catch(JSONException je) {
			throw new CommandExecuteException("El formato JSON del fichero introducido no es válido.", je);
		}
		
		return false;
	}
	
	/* Sobrescritura del método parse:
	 * Comprueba que los argumentos recibidos se correspondan con los del comando cargar,
	 * es decir, que el usuario haya introducido exactamente el nombre del comando y un nombre de fichero.
	 */
	
	@Override
	protected Command parse(String[] words) throws CommandParseException {
		
		if (!matchCommandName(words[0])) 
			return null;
		
		if (words.length != 2)
			throw new CommandParseException(String.format("[ERROR]: Comando %s: %s%n", words[0], INCORRECT_NUMBER_OF_ARGS_MSG));

		this.file = words[1];
		
		return this;
	}
}
