package storage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import factories.BoardBuilder;
import factories.BoxBuilder;
import factories.EasyPlayerBuilder;
import factories.GamePlayersBuilder;
import factories.GameTilesBuilder;
import factories.HardPlayerBuilder;
import factories.HumanPlayerBuilder;
import factories.MediumPlayerBuilder;
import factories.PlayerBuilder;
import factories.TileBuilder;
import factories.WordsBuilder;
import gameContainers.Board;
import gameContainers.GamePlayers;
import gameContainers.GameTiles;
import gameLogic.Game;
import gameUtils.StringUtils;

public class GameLoader {
	
	private static final String wordsFile = "words.json";
	public static final String NEW_GAME = "new_game.json";

	private static Scanner _scanner;
	
	private static BoardBuilder boardBuilder;
	private static GameTilesBuilder gameTilesBuilder;
	private static GamePlayersBuilder gamePlayersBuilder;
	private static WordsBuilder wordsBuilder = new WordsBuilder();
	
	public static Game reset(Game game) throws FileNotFoundException {
		return createGame(new FileInputStream(NEW_GAME), game);
	}
	
	public static Game loadGame(Game game, String file) throws FileNotFoundException {
		
		Game _game = createGame(new FileInputStream(file), game);
		System.out.println(StringUtils.LINE_SEPARATOR + "La partida se ha cargado con exito." + StringUtils.LINE_SEPARATOR);
			
		return _game;
	}
	
	private static Game createGame(InputStream input, Game game) {
		
		JSONObject json = new JSONObject(new JSONTokener(input));
		
		int currentTurn = json.getInt("current_turn"); // -1 si es partida nueva
		int numConsecutivePassedTurns = json.getInt("consecutive_turns_passed");
		boolean wordsInBoard = json.getBoolean("words_in_board");
		boolean gameFinished = json.getBoolean("game_finished");
		
		GamePlayers players = gamePlayersBuilder.createInstance(json.getJSONObject("game_players"));
		
		if(players.getNumPlayers() == 0)
			players = createPlayers();
		
		GameTiles tiles = gameTilesBuilder.createInstance(json.getJSONObject("game_tiles"));
		Board board = boardBuilder.createInstance(json.getJSONObject("game_board"));
		
		List<String> usedWords = wordsBuilder.createInstance(json.getJSONObject("used_words"));
		
		if(game == null)
			return new Game(currentTurn, numConsecutivePassedTurns, wordsInBoard, gameFinished, 
				players, tiles, board, usedWords);
		
		else {
			game.reset(currentTurn, numConsecutivePassedTurns, wordsInBoard, gameFinished, 
					players, tiles, board, usedWords);
			
			return game;
		}
	}
	
	public static Game initGame(Scanner scanner) throws FileNotFoundException {
		
		_scanner = scanner;
		
		System.out.println("¡Bienvenido a Scrabble!" + StringUtils.LINE_SEPARATOR);

		System.out.println("Opciones de inicio:");
		System.out.println("1. Nueva partida.");
		System.out.println("2. Cargar partida de fichero.");
		
		int option = 0;
		
		while(option != 1 && option != 2) {
			
			try {
				System.out.print(StringUtils.LINE_SEPARATOR + "Selecciona opcion: ");
				option = scanner.nextInt();
				
				if(option != 1 && option != 2)
					System.out.println("Opcion no valida.");
			}
			
			catch(InputMismatchException ime) {
				scanner.nextLine();
				System.out.println("Opcion no valida.");
			}
		}
		
		scanner.nextLine();
		
		// Nueva partida
		if(option == 1)
			return reset(null);
		
		// Carga de partida
		else {
			System.out.print("Introduce el nombre de fichero a cargar: ");
			String file = "partidas/" + _scanner.nextLine().trim() + ".json";
			return loadGame(null, file);
		}
	}
	
	private static GamePlayers createPlayers() {
		
		int numPlayers = selectNumPlayers();
		
		JSONArray players = new JSONArray();
		
		while(players.length() < numPlayers) {
			
			System.out.print("Tipo del jugador " + (players.length() + 1) + " [facil, medio, dificil o humano]: ");
			String type = takeType(_scanner.nextLine().trim());
			
			if(type != null) {
				
				JSONObject player = new JSONObject();
				player.put("type", type);
				player.put("total_points", 0);
				
				if(type.equalsIgnoreCase("human_player")) {
					System.out.print("Nombre del jugador " + (players.length() + 1) + ": ");
					String name = _scanner.nextLine().trim();
					
					if(checkPlayerNames(name, players)) {
						player.put("name", name);
						players.put(player);
					}
					else 
						System.out.println("Ya hay un jugador con el nombre " + name);
				}
				else players.put(player);
			}
			else
				System.out.println("El tipo introducido no es válido.");
		}
		
		System.out.println();
		
		JSONObject data = new JSONObject();
		data.put("players", players);
		
		return gamePlayersBuilder.createInstance(data);
	}
	
	private static int selectNumPlayers() {
		
		int numPlayers = 0;
		boolean done = false;
		System.out.print("Selecciona el numero de jugadores (2-4): ");
		
		while (!done) {
			try {
				numPlayers = _scanner.nextInt();
				
				if (numPlayers < 2 || numPlayers > 4) {
					System.out.println("El numero de jugadores debe estar entre 2 y 4.");
					System.out.print("Selecciona el numero de jugadores (2-4): ");
				}
				else done = true;
				
			}
			catch (InputMismatchException ime) {
				System.out.println("¡La entrada debe ser un numero!");
				System.out.print("Selecciona el numero de jugadores (2-4): ");
				_scanner.nextLine();
			}
		}
		
		// Para que la entrada sea correcta.
		_scanner.nextLine();  
		
		return numPlayers;
	}

	public static List<String> loadWordList() throws RuntimeException {
		
		try {
			JSONObject jo = new JSONObject(new JSONTokener(new FileInputStream(wordsFile)));
			return wordsBuilder.createInstance(jo);
		}
		catch(FileNotFoundException fnfe) {
			throw new RuntimeException("El fichero de las palabras no es válido");
		}
	}
	
	public static void initBuilders() {
		
		TileBuilder tileBuilder = new TileBuilder();
		
		boardBuilder = new BoardBuilder(new BoxBuilder(tileBuilder));
		
		gameTilesBuilder = new GameTilesBuilder(tileBuilder);
		
		List<PlayerBuilder> playerBuilders= new ArrayList<PlayerBuilder>();
		playerBuilders.add(new EasyPlayerBuilder(tileBuilder));
		playerBuilders.add(new MediumPlayerBuilder(tileBuilder));
		playerBuilders.add(new HardPlayerBuilder(tileBuilder));
		playerBuilders.add(new HumanPlayerBuilder(tileBuilder));
		
		gamePlayersBuilder = new GamePlayersBuilder(playerBuilders);
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
}
