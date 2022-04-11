package command;

import exceptions.CommandExecuteException;
import exceptions.CommandParseException;
import scrabble.Controller;

/* APUNTES GENERALES:
   
   La clase Command es una clase abstracta que 
   encapsula la principal funcionalidad de los comandos.
   
   Cada comando tiene un nombre (name), un atajo (shortcut),
   su formato de entrada (details) y una descripcion (help).
   
   El metodo parse es sobreescrito en LoadCommand, SaveCommand y WriteWordCommand.
   
   El metodo execute, al ser abstracto, es sobreescrito por todas los comandos que
   heredan de esta clase.
   
 */
public abstract class Command {
	
	private static final String UNKNOWN_COMMAND_MSG = "Comando desconocido";
	protected static final String INCORRECT_NUMBER_OF_ARGS_MSG = "Número incorrecto de argumentos";

	/* @formatter:off */
	protected static final Command[] AVAILABLE_COMMANDS = {
		new SkipCommand(),
		new ExitCommand(),
		new HelpCommand(),
		new NewGameCommand(),
		new ResetCommand(),
		new SwapTileCommand(),
		new LoadCommand(),
		new SaveCommand(),
		new WriteWordCommand()

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

	// Métodos comunes (posiblemente sobrescribibles) para todos los comandos 

	/* Método matchCommandName:
	 * Devuelve un booleano indicando si el nombre del comando introducido
	 * se corresponde con el nombre establecido o con su atajo.
	 * Este método nunca es sobrescrito.
	 */
	protected boolean matchCommandName(String name) {
		return this.shortcut.equalsIgnoreCase(name) || this.name.equalsIgnoreCase(name);
	}

	/* Método parse:
	 * Para cualquier comando que no sobreescriba este método, el método parse devuelve 
	 * el comando correspondiente en caso de que haya sido introducido correctamente por 
	 * el usuario, y en caso contrario, devuelve null.
	 * Este método es sobrescrito por LoadCommand, SaveCommand y WriteWordCommand.
	 */
	protected Command parse(String[] words) throws CommandParseException {
		if (matchCommandName(words[0])) {
			
			if (words.length != 1) 
				throw new CommandParseException(String.format("[ERROR]: Comando %s: %s%n", name ,INCORRECT_NUMBER_OF_ARGS_MSG));
			
			else return this;
			
		}
		
		return null;
	}
	
	/* Método getCommand:
	 * Recibe un supuesto comando del usuario.
	 * Si no existe, se devuelve null.
	 * Si existe, se devuelve el comando asociado.
	 * Este método nunca es sobrescrito.
	 */
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

	// Getters
	
	public String getDetails() {
		return this.details;
	}
	
	public String getHelp() {
		return this.help;
	}

	// Métodos abstractos
	
	/* Método execute:
	 * Cada comando que hereda de esta clase sobreescribe su propio método execute con su 
	 * funcionalidad concreta. El método devuelve un booleano indicando si el jugador actual 
	 * debe seguir jugando su turno o pasar al siguiente.
	 */
	public abstract boolean execute(Controller controller) throws CommandExecuteException;

}
