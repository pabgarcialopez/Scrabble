package command;

import exceptions.*;
import gameLogic.Game;

public abstract class Command {
	private static final String UNKNOWN_COMMAND_MSG = "Comando desconocido";

	protected static final String INCORRECT_NUMBER_OF_ARGS_MSG = "Número incorrecto de argumentos";

	/* @formatter:off */
	protected static final Command[] AVAILABLE_COMMANDS = {
		new WriteWordCommand(),
		new PassTurnCommand(),
		new SwapTileCommand(),
		new HelpCommand(),
		new ExitCommand(),
		new LoadCommand(),
		new ResetCommand(),
		new SaveCommand()
	};
	/* @formatter:on */

	private final String name;

	private final String shortcut;

	private final String details;

	private final String help;

	public Command(String name, String shortcut, String details, String help) {
		this.name = name;
		this.shortcut = shortcut;
		this.details = details;
		this.help = help;
	}

	public abstract boolean execute(Game game) throws CommandExecuteException;

	protected boolean matchCommandName(String name) {
		return this.shortcut.equalsIgnoreCase(name) || this.name.equalsIgnoreCase(name);
	}

	protected Command parse(String[] words) throws CommandParseException {
		if (matchCommandName(words[0])) {
			
			if (words.length != 1) 
				throw new CommandParseException(String.format("[ERROR]: Comando %s: %s%n", name ,INCORRECT_NUMBER_OF_ARGS_MSG));
			
			else return this;
			
		}
		
		return null;
	}
	
	public static Command getCommand(String[] commandWords) throws CommandParseException {
		
		Command command = null;
		
		int i = 0;
		
		while (command == null && i < AVAILABLE_COMMANDS.length) {
			command = AVAILABLE_COMMANDS[i].parse(commandWords);
			++i;
		}
		
		if (command == null) {
			throw new CommandParseException(String.format("[ERROR]: %s%n", UNKNOWN_COMMAND_MSG));
		}
		
		return command;
	}


	protected String getDetails() {
		return this.details;
	}
	
	protected String getHelp() {
		return this.help;
	}

}
