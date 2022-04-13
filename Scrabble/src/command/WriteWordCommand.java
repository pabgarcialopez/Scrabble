package command;

import java.io.PrintStream;
import java.util.Scanner;

import exceptions.CommandParseException;
import scrabble.Controller;

public class WriteWordCommand extends Command {

	private static final String NAME = "palabra";

	private static final String DETAILS = "[p]alabra [palabra a colocar] [dirección('V'/'H')] [fila] [columna]";

	private static final String SHORTCUT = "p";

	private static final String HELP = "colocar palabra";
	
	private String word;
	
	private int posX;
	
	private int posY;
	
	private String direction;
	
	public WriteWordCommand() {
		super(NAME, SHORTCUT, DETAILS, HELP);
	}
	
	@Override
	public boolean execute(Controller controller, Scanner in, PrintStream out) {
		
		return !controller.writeAWord(word, posX, posY, direction);
	}
	
	@Override
	protected Command parse(String[] words) throws CommandParseException {
		
		if (!matchCommandName(words[0])) return null;
		
		if (words.length != 5)
			throw new CommandParseException(String.format("[ERROR]: Comando %s: %s%n", words[0] ,INCORRECT_NUMBER_OF_ARGS_MSG));

		try {
			this.word = words[1];
			this.direction = words[2];
			this.posX = Integer.parseInt(words[3]);
			this.posY = Integer.parseInt(words[4]);
		}
		
		catch(NumberFormatException nfe) {
			throw new CommandParseException("[ERROR]: la posición debe ser dos números");
		}
		
		return this;		
	}
}
