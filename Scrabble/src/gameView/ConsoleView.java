package gameView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
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
	private static final String ROW_SEPARATOR_SYMBOL = "-";
	private static final String COLUMN_SEPARATOR_SYMBOL = "|";
	
	private static final String[] PLAYER_TYPES = {"humano", "fácil", "medio", "difícil"};
	
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
		this.out.print(game.obtainStatus());
	}

	private void showFirstTurn(String[] lettersObtained, GamePlayers players, int turn) {
		
		StringBuilder buffer = new StringBuilder();
		buffer.append("Elección de turnos:").append(StringUtils.LINE_SEPARATOR);
		
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
		
		pausa();
	}

	public void showBoard(Game game) {
		
		// Numero de digitos del tamaño del tablero.
		int max_indentation_length = (int) Math.log10(game.getBoardSize()) + 1;
		String max_left_indentation = createIndentation(max_indentation_length) + "   ";
		
		// Numero de "ROW_SEPARATOR_SYMBOL", que deben separar filas.
		int row_separator_length = 0;
		
		// Indentación para la primera fila de números.
		this.out.print(max_left_indentation);
		
		// Imprimimos línea de coordenadas del lado superior
		for(int i = 0; i < game.getBoardSize(); i++) {
			
			// Espacio extra que deben tener numeros de ciertas cifras
			int separationBetweenNumbers = max_indentation_length - (i == 0 ? 1 : (int) Math.log10(i) + 1);
			
			if(max_indentation_length % 2 == 0) {
				this.out.print(createIndentation(max_indentation_length / 2) + i + createIndentation(max_indentation_length / 2 + separationBetweenNumbers));
				row_separator_length += max_indentation_length + separationBetweenNumbers + (i == 0 ? 1 : (int) Math.log10(i) + 1);
			}
			
			else {
				this.out.print(createIndentation(max_indentation_length / 2 + 1) + i + createIndentation(max_indentation_length / 2 + separationBetweenNumbers + 2));
				row_separator_length += max_indentation_length + separationBetweenNumbers + 2 + (i == 0 ? 1 : (int) Math.log10(i) + 1);
			}
		}
		
		this.out.print(StringUtils.LINE_SEPARATOR);
		
		// Imprimimos el resto del tablero
		for(int i = 0; i < game.getBoardSize(); i++) {
			
			printRowSeparator(row_separator_length - 1, max_left_indentation);
				
			// Imprimir número de fila
			this.out.print(" ");
			
			int i_indentation_length = max_indentation_length - (i == 0 ? 1 : (int) Math.log10(i) + 1);
			String actual_left_indentation = createIndentation(i_indentation_length);
			
			this.out.print(actual_left_indentation + i + " ");
			
			// Imprimir lo correspondiente a esa fila
			for(int j = 0; j < game.getBoardSize(); j++) {
				Box box = game.getBoxAt(i, j);
				String boxContent = box.toString();
				
				if("".equals(boxContent)) {
					SpecialEffects speEff = box.getSpecialEffect();
					
					if(speEff != null) {
						switch(speEff) {
							case CENTRE: boxContent = CENTRE_SYMBOL; break;
							case DOUBLE_LETTER: boxContent = DOUBLE_LETTER_SYMBOL; break;
							case DOUBLE_WORD: boxContent = DOUBLE_WORD_SYMBOL; break;
							case TRIPLE_LETTER: boxContent = TRIPLE_LETTER_SYMBOL; break;
							case TRIPLE_WORD: boxContent = TRIPLE_WORD_SYMBOL; break;
							default: boxContent = " "; break;
						}
					}
					
					else boxContent = " ";
				}
				
				if(max_indentation_length % 2 == 0)
					this.out.print(COLUMN_SEPARATOR_SYMBOL + createIndentation(max_indentation_length/2) + boxContent + createIndentation(max_indentation_length/2));
				
				else this.out.print(COLUMN_SEPARATOR_SYMBOL + createIndentation(max_indentation_length/2 + 1) + boxContent + createIndentation(max_indentation_length/2 + 1));
			}
				
			this.out.print(COLUMN_SEPARATOR_SYMBOL + StringUtils.LINE_SEPARATOR);
		}
		
		printRowSeparator(row_separator_length - 1, max_left_indentation);
		
		this.out.print("Doble letra: " + DOUBLE_LETTER_SYMBOL + " || " +
				         "Doble palabra: " + DOUBLE_WORD_SYMBOL + " || " +
				         "Triple letra: " + TRIPLE_LETTER_SYMBOL + " || " + 
				         "Triple palabra: " + TRIPLE_WORD_SYMBOL + 
				         StringUtils.LINE_SEPARATOR);
		
		this.out.print(StringUtils.LINE_SEPARATOR);
	}
	
	private String createIndentation(int length) {
		String indentation = "";
		for(int i = 0; i < length; i++)
			indentation += " ";
		return indentation;
	}
	
	private void printRowSeparator(int row_separator_length, String max_left_indentation) {
		
		this.out.print(max_left_indentation);
		for(int i = 0; i < row_separator_length; i++)
			this.out.print(ROW_SEPARATOR_SYMBOL);
		this.out.print(StringUtils.LINE_SEPARATOR);
	}

	public void showEndMessage(String winners) {
		this.out.print(StringUtils.LINE_SEPARATOR);
		this.out.println(winners);
		this.out.println("¡Gracias por jugar!");
	}

	@Override
	public void onWordWritten(Game game, String word, int posX, int posY, String direction, int points,
			int extraPoints) {
		this.out.println(String.format(
				"El jugador %s escribe la palabra \"%s\" en la posición (%s, %s) en dirección \"%s\".%n",
				game.getPlayers().getPlayerName(game.getCurrentTurn()), word.toUpperCase(), posX, posY, direction.toUpperCase()));
		this.out.print(String.format("¡Gana %s puntos!", points));
		if(extraPoints != 0)
			this.out.println(String.format(" Además, ¡gana %s puntos extra!%n", extraPoints));
		else
			this.out.print(StringUtils.DOUBLE_LINE_SEPARATOR);
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
		if (!Game.getGameInitiated()) {
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
		playTurn();
	}

	@Override
	public void onError(String error) {
		this.out.println(error);
		playTurn();
	}

	@Override
	public void onUpdate(Game game) {
		
		showBoard(game);
		
		if(!game.gameIsFinished()) {
			this.humanIsPlaying = game.humanIsPlaying();
			showStatus(game);
			playTurn();
		}
	}

	@Override
	public void onEnd(String message) {
		showEndMessage(message);
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
	
	private void playTurn() {
		
		Command command = null;
		
		if(this.humanIsPlaying) {
			
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
				playTurn();
			
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
		this.out.println("Pulsa enter para continuar...");
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
			
			this.out.print("Tipo del jugador " + (players.length() + 1) + " " + Arrays.asList(PLAYER_TYPES).toString() + ": ");
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
		this.out.print("Selecciona el número de jugadores (2-4): ");
		
		while (!done) {
			try {
				numPlayers = this.in.nextInt();
				
				if (numPlayers < 2 || numPlayers > 4) {
					this.out.println("El número de jugadores debe estar entre 2 y 4.");
					this.out.print("Selecciona el número de jugadores (2-4): ");
				}
				else done = true;
				
			}
			catch (InputMismatchException ime) {
				this.out.println("¡La entrada debe ser un número!");
				this.out.print("Selecciona el número de jugadores (2-4): ");
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

		type = StringUtils.removeAccents(type.toLowerCase());
		
		switch(type) {
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
				this.out.print(StringUtils.LINE_SEPARATOR + "Selecciona opción: ");
				option = this.in.nextInt();
				
				if(option != 1 && option != 2)
					this.out.println("Opción no valida.");
			}
			
			catch(InputMismatchException ime) {
				this.in.nextLine();
				this.out.println("Opción no valida.");
			}
		}
		
		this.in.nextLine();
		
		// Nueva partida
		if(option == 1)
			this.controller.newGame();
		
		else {
			
			File dir = new File("resources/existingGames");
			File[] files = dir.listFiles();
			
			if(files.length != 0) {
				this.out.print(StringUtils.LINE_SEPARATOR);
				this.out.print("Las partidas disponibles son:" + StringUtils.LINE_SEPARATOR);
				
				for(File file: files) {
					String fileName = file.getName();
					this.out.println("|--> " + fileName);
				}
				
				this.out.print(StringUtils.LINE_SEPARATOR);
				this.out.print("Introduce el nombre de la partida a cargar: ");
				
				String file = this.in.nextLine();
				
				if(!file.endsWith(".json"))
					file += ".json";
				
				while(!(new File("resources/existingGames/" + file)).exists()) {
					this.out.print(StringUtils.LINE_SEPARATOR);
					this.out.println("No existe una partida con el nombre " + "\"" + file + "\"");
					
					this.out.print("Introduce el nombre de la partida a cargar: ");
					
					file = this.in.nextLine();
					
					if(!file.endsWith(".json"))
						file += ".json";
				}
				
				this.out.print(StringUtils.LINE_SEPARATOR);
				
				this.controller.loadGame("resources/existingGames/" + file);
			}
			
			else {
				this.out.print(StringUtils.LINE_SEPARATOR);
				this.out.print("No existen partidas anteriores.");
				this.out.print(StringUtils.LINE_SEPARATOR);
			}
		}
	}
}