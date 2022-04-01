package gameView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.InputMismatchException;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import command.Command;
import exceptions.CommandExecuteException;
import exceptions.CommandParseException;
import gameContainers.GamePlayers;
import gameLogic.Game;
import gameObjects.Box;
import gameObjects.SpecialEffects;
import gameUtils.StringUtils;
import scrabble.Controller;
import storage.GameLoader;

public class ConsoleView implements ScrabbleObserver {
	
	private static final String PROMPT = "Comando ([h]elp) > ";
	private static final String CENTRE_SYMBOL = "*";
	private static final String DOUBLE_LETTER_SYMBOL = "•";
	private static final String DOUBLE_WORD_SYMBOL = "░";
	private static final String TRIPLE_LETTER_SYMBOL = "▒";
	private static final String TRIPLE_WORD_SYMBOL = "█";
	
	private Controller controller;
	
	private Scanner in;
	
	private PrintStream out;
	
	private boolean humanIsPlaying;
	
	public ConsoleView(Controller controller, InputStream in, OutputStream out) {
		this.controller = controller;
		this.in = new Scanner(in);
		this.out = new PrintStream(out);
		this.controller.addObserver(this);
	}

	public void showStatus(Game game) {
		this.out.println(game.getStatus());
	}

	// Funcion para mostrar como ha sido la decision de quien empieza a jugar
	private void showFirstTurn(String[] lettersObtained, GamePlayers players, int turn) {
		
		StringBuilder buffer = new StringBuilder();
		buffer.append("Eleccion de turnos:").append(StringUtils.LINE_SEPARATOR);
		
		for(int i = 0; i < players.getNumPlayers(); i++) {
			buffer.append(players.getPlayerName(i)).append(" ha cogido una ")
				  .append(lettersObtained[i]).append(StringUtils.LINE_SEPARATOR);
		}
		
		buffer.append(StringUtils.LINE_SEPARATOR).append("El orden de juego es: ");
		
		for(int i = 0; i < players.getNumPlayers(); i++) {
			buffer.append(players.getPlayerName((i + turn) % players.getNumPlayers()));
			if(i != players.getNumPlayers() - 1) buffer.append(" -> ");
		}
			
		
		buffer.append(StringUtils.LINE_SEPARATOR);
		
		this.out.println(buffer);
	}

	public void showBoard(Game game) {
		
		this.out.print("   "); // Espacio de indentacion
		// Imprimimos linea de coordenadas del lado superior
		for(int i = 0; i < game.getBoardSize(); i++) {
			if(i < 10) this.out.print(" " + i + "  ");
			else this.out.print(" " + i + " ");
		}
		
		this.out.print(StringUtils.LINE_SEPARATOR);
		
		// Imprimimos el resto del tablero
		for(int i = 0; i < game.getBoardSize(); i++) {
			
			this.out.print("   "); // Espacio de indentacion
			for(int k = 0; k < game.getBoardSize(); k++)
				this.out.print("----");
			
			this.out.print(StringUtils.LINE_SEPARATOR);
				
			// Imprimir numero de fila
			if(i < 10) this.out.print(" " + i);
			else this.out.print(i);
			
			// Imprimir lo correspondiente a esa fila
			for(int j = 0; j < game.getBoardSize(); j++) {
				Box box = game.getBoxAt(i, j);
				String boxContent = box.toString();
				
				if(" ".equals(boxContent)) {
					SpecialEffects speEff = box.getSpecialEffect();
					
					if(SpecialEffects.CENTRE.equals(speEff))
						boxContent = CENTRE_SYMBOL;
					else if(SpecialEffects.DOUBLE_LETTER.equals(speEff))
						boxContent = DOUBLE_LETTER_SYMBOL;
					else if(SpecialEffects.DOUBLE_WORD.equals(speEff))
						boxContent = DOUBLE_WORD_SYMBOL;
					else if(SpecialEffects.TRIPLE_LETTER.equals(speEff))
						boxContent = TRIPLE_LETTER_SYMBOL;
					else if(SpecialEffects.TRIPLE_WORD.equals(speEff))
						boxContent = TRIPLE_WORD_SYMBOL;
				}
				
				this.out.print("|" + " " + boxContent + " ");
			}
				
			this.out.print("|" + StringUtils.LINE_SEPARATOR);
		}
		
		this.out.print("   "); // Espacio de indentacion
		for(int k = 0; k < game.getBoardSize(); k++)
			this.out.print("----");
		
		this.out.print(StringUtils.LINE_SEPARATOR);
		
		this.out.print("Doble letra: " + DOUBLE_LETTER_SYMBOL + " || " +
				         "Doble palabra: " + DOUBLE_WORD_SYMBOL + " || " +
				         "Triple letra: " + TRIPLE_LETTER_SYMBOL + " || " + 
				         "Triple palabra: " + TRIPLE_WORD_SYMBOL + 
				         StringUtils.LINE_SEPARATOR);
		
		this.out.print(StringUtils.LINE_SEPARATOR);
	}

	public void showEndMessage() {
		this.out.println("Gracias por jugar!");
	}

	@Override
	public void onWordWritten(Game game, String word, int posX, int posY, String direction, int points,
			int extraPoints) {
		this.out.println(String.format(
				"El jugador %s escribe la palabra \"%s\" en la posicion (%s, %s) en dirección \"%s\".%n",
				game.getPlayers().getPlayerName(game.getCurrentTurn()), word.toUpperCase(), posX, posY, direction.toUpperCase()));
		this.out.print(String.format("¡Gana %s puntos!", points));
		if(extraPoints != 0)
			this.out.println(String.format(" Además, ¡gana %s puntos extra!%n", extraPoints));
		else
			this.out.println(StringUtils.LINE_SEPARATOR);
	}

	@Override
	public void onPassed(Game game) {
		this.out.println(String.format("El jugador %s pasa de turno.%n", game.getPlayers().getPlayerName(game.getCurrentTurn())));
	}

	@Override
	public void onSwapped(Game game) {
		this.out.println(String.format("El jugador %s intercambia una ficha.%n", game.getPlayers().getPlayerName(game.getCurrentTurn())));
	}

	@Override
	public void onRegister(Game game) {
		if (!game.getGameInitiated()) {
			try {
				initGame();
			} catch (FileNotFoundException fnfe) {
				throw new RuntimeException("No ha sido posible iniciar la partida", fnfe);
			}
		}
	}

	@Override
	public void onReset(Game game) {
		
		this.out.println("Partida iniciada con éxito." + StringUtils.LINE_SEPARATOR);
		this.humanIsPlaying = game.humanIsPlaying();
		showBoard(game);
		showStatus(game);
		makeTurn();
	}

	@Override
	public void onError(String error) {
		this.out.println(error);
		makeTurn();
	}

	@Override
	public void onUpdate(Game game) {
		
		showBoard(game);
		
		if(!game.gameIsFinished()) {
			this.humanIsPlaying = game.humanIsPlaying();
			showStatus(game);
			makeTurn();
		}
	}

	@Override
	public void onEnd() {
		showEndMessage();
		System.exit(0);
	}
	
	@Override
	public void onFirstTurnDecided(Game game, String[] lettersObtained) {
		showFirstTurn(lettersObtained, game.getPlayers(), game.getCurrentTurn());
	}
	
	@Override
	public void onPlayersNotAdded(Game game) {
		this.controller.addPlayers(createPlayers());
	}
	
	private void makeTurn() {
		
		Command command = null;
		
		if(this.humanIsPlaying) {
			while (command == null) {
				command = askCommand();
			}
			
			boolean makeAnotherTurn = true;
			
			try {
				makeAnotherTurn = command.execute(this.controller);
			}
			catch(CommandExecuteException cee) {
				this.out.println(cee.getMessage() + StringUtils.LINE_SEPARATOR);
			}
			
			if(makeAnotherTurn) {
				makeTurn();
			}
			else {				
				pausa();
				controller.update();
			}
		}
		else {
			controller.automaticPlay();
			pausa();
			controller.update();
		}
	}
	
	private void pausa() {
		this.out.println("Pulse enter para continuar...");
		this.in.nextLine();
	}

	private Command askCommand() {
		
		Command command = null;
		
		this.out.print(PROMPT);
		String s = this.in.nextLine();

		String[] parameters = s.toLowerCase().trim().split(" ");
		
		try {
			command = Command.getCommand(parameters);
		}
		catch(CommandParseException cpe) {
			this.out.println(cpe.getMessage());
		}
		
		return command;		
	}
	
	private GamePlayers createPlayers() {
		
		int numPlayers = selectNumPlayers();
		
		JSONArray players = new JSONArray();
		
		while(players.length() < numPlayers) {
			
			this.out.print("Tipo del jugador " + (players.length() + 1) + " [facil, medio, dificil o humano]: ");
			String type = takeType(this.in.nextLine().trim());
			
			if(type != null) {
				
				JSONObject player = new JSONObject();
				player.put("type", type);
				player.put("total_points", 0);
				
				if(type.equalsIgnoreCase("human_player")) {
					this.out.print("Nombre del jugador " + (players.length() + 1) + ": ");
					String name = this.in.nextLine().trim();
					
					if(checkPlayerNames(name, players)) {
						player.put("name", name);
						players.put(player);
					}
					else 
						this.out.println("Ya hay un jugador con el nombre " + name);
				}
				else players.put(player);
			}
			else
				this.out.println("El tipo introducido no es válido.");
		}
		
		this.out.println();
		
		JSONObject data = new JSONObject();
		data.put("players", players);
	
		return GameLoader.createPlayers(data);
	}
		
	private int selectNumPlayers() {
			
		int numPlayers = 0;
		boolean done = false;
		this.out.print("Selecciona el numero de jugadores (2-4): ");
		
		while (!done) {
			try {
				numPlayers = this.in.nextInt();
				
				if (numPlayers < 2 || numPlayers > 4) {
					this.out.println("El numero de jugadores debe estar entre 2 y 4.");
					this.out.print("Selecciona el numero de jugadores (2-4): ");
				}
				else done = true;
				
			}
			catch (InputMismatchException ime) {
				this.out.println("¡La entrada debe ser un numero!");
				this.out.print("Selecciona el numero de jugadores (2-4): ");
				this.in.nextLine();
			}
		}
		
		// Para que la entrada sea correcta.
		this.in.nextLine();  
		
		return numPlayers;
	}

	private static boolean checkPlayerNames(String name, JSONArray players) {
		
		int i = 0;
		while(i < players.length()) {
			if(players.getJSONObject(i).has("name") && players.getJSONObject(i).getString("name").equalsIgnoreCase(name))
				return false;
			++i;
		}
		
		return true;
	}
	
	private static String takeType(String type) {

		switch(type.toLowerCase()) {
		case "facil":
			return "easy_player";
		case "medio":
			return "medium_player";
		case "dificil":
			return "hard_player";
		case "humano":
			return "human_player";
		default:
			return null;
		}
	}

	private void initGame() throws FileNotFoundException {
		
		this.out.println("¡Bienvenido a Scrabble!" + StringUtils.LINE_SEPARATOR);

		this.out.println("Opciones de inicio:");
		this.out.println("1. Nueva partida.");
		this.out.println("2. Cargar partida de fichero.");
		
		int option = 0;
		
		while(option != 1 && option != 2) {
			
			try {
				this.out.print(StringUtils.LINE_SEPARATOR + "Selecciona opcion: ");
				option = this.in.nextInt();
				
				if(option != 1 && option != 2)
					this.out.println("Opcion no valida.");
			}
			
			catch(InputMismatchException ime) {
				this.in.nextLine();
				this.out.println("Opcion no valida.");
			}
		}
		
		this.in.nextLine();
		
		// Nueva partida
		if(option == 1)
			this.controller.reset();
		else {
			
			File dir = new File("partidas");
			File[] files = dir.listFiles();
			
			if(files.length != 0) {
				this.out.print(StringUtils.LINE_SEPARATOR);
				this.out.print("Las partidas disponibles son:" + StringUtils.LINE_SEPARATOR);
				
				
				for(File file: files) {
					String fileName = file.getName();
					String[] fileComponents = fileName.split("\\.");
					this.out.println("|--> " + fileComponents[0]);
				}
			}
				
			this.out.print(StringUtils.LINE_SEPARATOR);
			this.out.print("Introduce el nombre de la partida a cargar: ");
			String fileWithNoExtension = this.in.nextLine().trim();
			String file = "partidas/" + fileWithNoExtension + ".json";
			
			while(!(new File(file)).exists()) {
				this.out.print(StringUtils.LINE_SEPARATOR);
				this.out.println("No existe una partida con el nombre " + "\"" + fileWithNoExtension + "\"");
				
				this.out.print("Introduce el nombre de la partida a cargar: ");
				fileWithNoExtension = this.in.nextLine().trim();
				file = "partidas/" + fileWithNoExtension + ".json";
			}
			
			this.controller.loadGame(file);
		}
	}
}