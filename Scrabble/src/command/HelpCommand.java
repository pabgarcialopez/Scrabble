package command;

import gameLogic.Game;
import gameUtils.StringUtils;

public class HelpCommand extends Command {

	private static final String NAME = "ayuda";

	private static final String DETAILS = "[a]yuda";

	private static final String SHORTCUT = "a";

	private static final String HELP = "muestra esta ayuda";

	public HelpCommand() {
		super(NAME, SHORTCUT, DETAILS, HELP);
	}

	@Override
	public boolean execute(Game game) {
		
		StringBuilder buffer = new StringBuilder("Comandos disponibles:").append(StringUtils.LINE_SEPARATOR);
		
		for (int i = 0; i < AVAILABLE_COMMANDS.length;++i) {
			buffer.append(AVAILABLE_COMMANDS[i].getDetails()).append(": ").append(AVAILABLE_COMMANDS[i].getHelp())
			.append(StringUtils.LINE_SEPARATOR);
		}
		buffer.append(StringUtils.LINE_SEPARATOR);

		System.out.print(buffer.toString());
		
		return false;
	}
}
