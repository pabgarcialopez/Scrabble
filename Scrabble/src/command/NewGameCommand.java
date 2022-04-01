package command;

import java.io.FileNotFoundException;

import exceptions.CommandExecuteException;
import scrabble.Controller;

public class NewGameCommand extends Command{

	private static final String NAME = "nueva";

	private static final String DETAILS = "[n]ueva";

	private static final String SHORTCUT = "n";

	private static final String HELP = "Empezar una nueva partida";
	
	public NewGameCommand() {
		super(NAME, SHORTCUT, DETAILS, HELP);
	}
	
	@Override
	public boolean execute(Controller controller) throws CommandExecuteException {
		
		try {
			controller.newGame();
		}
		catch(FileNotFoundException fnfe) {
			throw new CommandExecuteException("El fichero de nueva partida no se ha podido encontrar.", fnfe);
		}
		
		return false;
	}
}
