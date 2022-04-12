package simulatedObjects;

import command.Command;
import exceptions.CommandExecuteException;
import exceptions.CommandParseException;
import logic.Game;
import utils.StringUtils;

public class HumanStrategy implements Strategy{
	
	@Override
	public void play(Game game) {
		Command command = null;
		
		//if(this.humanIsPlaying) {
			
			while (command == null)
				command = askCommand();
			
			boolean playAnotherTurn = true;
			
			try {
				playAnotherTurn = command.execute(this.controller);
			}
			
			catch(CommandExecuteException cee) {
				this.out.println(cee.getMessage() + StringUtils.LINE_SEPARATOR);
			}
			
			if(playAnotherTurn)
				game.playTurn();
			
			else {				
				pausa();
				controller.update();
			}
	}
	
	private Command askCommand() {
		
		Command command = null;
		
		this.out.print(PROMPT);
		String s = this.in.nextLine();
		
		s = StringUtils.removeAccents(s);
		
		if(this.in != consoleInput)
			this.out.print(s + StringUtils.LINE_SEPARATOR);

		String[] parameters = s.toLowerCase().trim().split(" ");
		
		try {
			command = Command.getCommand(parameters);
		}
		catch(CommandParseException cpe) {
			this.out.println(cpe.getMessage());
		}
		
		return command;		
	}

}
