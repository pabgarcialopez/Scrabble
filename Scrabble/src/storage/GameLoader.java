package storage;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.json.JSONObject;
import org.json.JSONTokener;

import factories.BoardBuilder;
import factories.BoxBuilder;
import factories.Factory;
import factories.GamePlayersBuilder;
import factories.GameTilesBuilder;
import factories.PlayerBuilder;
import factories.TileBuilder;
import factories.WordsBuilder;
import gameContainers.Board;
import gameContainers.GamePlayers;
import gameContainers.GameTiles;
import gameLogic.Game;
import gameObjects.Box;
import gameObjects.Player;
import gameObjects.SpecialEffects;
import gameObjects.Tile;

public class GameLoader {
	
	private static final String wordsFile = "words.txt";
	private static final String NEW_GAME = "new_game.json";

	private static Scanner _scanner;
	
	private static BoardBuilder boardBuilder;
	private static GameTilesBuilder gameTilesBuilder;
	private static GamePlayersBuilder gamePlayersBuilder;
	private static WordsBuilder wordsBuilder;
	
	public static Game reset() throws FileNotFoundException {
		return createGame(new FileInputStream(NEW_GAME));
	}
	
	public static Game loadGame() throws FileNotFoundException {
		System.out.print("Introduce el nombre de fichero a cargar: ");
		String file = _scanner.nextLine() + ".json";
		return createGame(new FileInputStream(file));
	}
	
	private static Game createGame(InputStream input) {
		
		JSONObject json = new JSONObject(new JSONTokener(input));
		
		int currentTurn = json.getInt("current_turn"); // -1 si es partida nueva
		int numConsecutivePassedTurns = json.getInt("consecutive_turns_passed");
		int numTurnsWithoutTiles = json.getInt("turns_without_tiles"); // -1 si es partida nueva
		boolean wordsInBoard = json.getBoolean("words_in_board");
		boolean gameFinished = json.getBoolean("game_finished");
		
		GamePlayers players = gamePlayersBuilder.createInstance(json.getJSONObject("game_players"));
		
		if(players.getNumPlayers() == 0)
			players = createPlayers();
		
		GameTiles tiles = gameTilesBuilder.createInstance(json.getJSONObject("game_tiles"));
		Board board = boardBuilder.createInstance(json.getJSONObject("game_board"));
		
		List<String> usedWords = wordsBuilder.createInstance(json.getJSONObject("used_words"));
		
		return new Game(currentTurn, numConsecutivePassedTurns, numTurnsWithoutTiles, wordsInBoard, gameFinished, 
				players, tiles, board, usedWords);
	}
	
	public static Game initGame(Scanner scanner) throws FileNotFoundException {
		
		_scanner = scanner;
		
		TileBuilder tileBuilder = new TileBuilder();
		boardBuilder = new BoardBuilder(new BoxBuilder(tileBuilder));
		gameTilesBuilder = new GameTilesBuilder(tileBuilder);
		gamePlayersBuilder = new GamePlayersBuilder(new PlayerBuilder(tileBuilder));
		wordsBuilder = new WordsBuilder();
		
		System.out.println("Opciones de inicio:");
		System.out.println("1. Nueva partida.");
		System.out.println("2. Cargar partida de fichero.");
		
		int option = 0;
		
		while(option != 1 && option != 2) {
			
			try {
				System.out.print("Selecciona opcion: ");
				option = scanner.nextInt();
				
				if(option != 1 && option != 2)
					System.out.println("Opcion no valida.");
			}
			
			catch(InputMismatchException ime) {
				scanner.nextLine();
				System.out.println("Opcion no valida.");
			}
		}
		
		// Nueva partida
		if(option == 1) return reset();
		
		// Carga de partida
		else return loadGame();
		
	}
	
	private static GamePlayers createPlayers() {
		
		int numPlayers = selectNumPlayers();
		
		List<Player> players = new ArrayList<Player>();
		
		while(players.size() < numPlayers) {
			try {
				System.out.print("Nombre del jugador " + (players.size() + 1) + ": ");
				players.add(new Player(_scanner.nextLine()));
			}
			
			catch(IllegalArgumentException iae) {
				System.out.println(iae.getMessage());
			}
		}
		
		return new GamePlayers(players);
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
				System.out.println ("La entrada debe ser un número!");
				System.out.print("Selecciona el numero de jugadores (2-4): ");
				_scanner.nextLine();
			}
		}
		
		// Para que la entrada sea correcta.
		_scanner.nextLine();  
		
		return numPlayers;
	}

	private static ArrayList<String> words;

	public static ArrayList<String> loadWordList() {
		
		// HABRA QUE CAMBIARLO POR JSONS
		try(BufferedReader buffer = new BufferedReader(new FileReader(wordsFile))) {
			
			List<String> words = new ArrayList<String>();
			
			String linea = null;
			while((linea = buffer.readLine()) != null) {
				words.add(linea.trim());
			}
		}
		
		catch (IOException ioe) {
			System.out.println("Error al leer el fichero words.txt");
		}
		
		return words;
	}
}
