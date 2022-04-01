package command;

import gameUtils.StringUtils;
import scrabble.Controller;

public class HelpCommand extends Command {

	private static final String NAME = "help";

	private static final String DETAILS = "[h]elp";

	private static final String SHORTCUT = "h";

	private static final String HELP = "muestra esta ayuda";

	public HelpCommand() {
		super(NAME, SHORTCUT, DETAILS, HELP);
	}

	@Override
	public boolean execute(Controller controller) {
		
		StringBuilder buffer = new StringBuilder("Comandos disponibles:").append(StringUtils.LINE_SEPARATOR);
		
		for (int i = 0; i < AVAILABLE_COMMANDS.length;++i) {
			buffer.append(AVAILABLE_COMMANDS[i].getDetails()).append(": ").append(AVAILABLE_COMMANDS[i].getHelp())
			.append(StringUtils.LINE_SEPARATOR);
		}
		buffer.append(StringUtils.LINE_SEPARATOR);

		System.out.print(buffer.toString());
		
		return true;
	}
}
