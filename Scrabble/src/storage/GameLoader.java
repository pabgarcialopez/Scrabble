package storage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import containers.Board;
import containers.GamePlayers;
import containers.GameTiles;
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
import logic.Game;

/* APUNTES GENERALES:
   
   La clase GameLoader es la encargada de cargar una partida, ya sea nueva o existente.
   
   La clase tiene como atributos estáticos instancias de builders:
   - BoardBuilder
   - GameTilesBuilder
   - GamePlayersBuilder
   - WordsBuilder
   
 */
public class GameLoader {
	
	private static final String wordsFile = "resources/files/words.json";
	public static final String NEW_GAME = "resources/existingGames/new_game.json";
	
	private static BoardBuilder boardBuilder;
	private static GameTilesBuilder gameTilesBuilder;
	private static GamePlayersBuilder gamePlayersBuilder;
	private static WordsBuilder wordsBuilder = new WordsBuilder();
	
	/* Método newGame:
	 * Delega la creación de la partida al método createGame.
	 * En este caso, se crea una nueva partida (fichero NEW_GAME).
	 */
	public static void newGame(Game game) throws FileNotFoundException {
		createGame(new FileInputStream(NEW_GAME), game);
	}
	
	/* Método loadGame:
	 * Delega la creación de la partida al método createGame.
	 * En este caso, se carga la partida guardada en el fichero recibido por parámetro.
	 */
	public static void loadGame(Game game, String file) throws FileNotFoundException {
		
		if(!file.endsWith(".json"))
			file += ".json";
		
		createGame(new FileInputStream(file), game);
	}
	
	/* Método createGame:
	 * Recoge en un objeto JSON todos los datos necesarios para crear o cargar una partida.
	 * Delega la inicialización de los atributos del parámetro recibido de la clase Game, en
	 * el método reset.
	 */
	private static Game createGame(InputStream input, Game game) {
		
		JSONObject json = new JSONObject(new JSONTokener(input));
		
		if(!Game.getGameInitiated())
			Game.setSeed(json.has("seed") ? json.getInt("seed") : Game.getSeed());
		else Game.setSeed(Game.getSeed());
		
		game.resetPlayers();
		
		int currentTurn = json.getInt("current_turn"); // -1 si es partida nueva
		int numConsecutivePassedTurns = json.getInt("consecutive_turns_passed");
		boolean wordsInBoard = json.getBoolean("words_in_board");
		boolean gameFinished = json.getBoolean("game_finished");
		
		GamePlayers players = gamePlayersBuilder.createInstance(json.getJSONObject("game_players"));
		
		GameTiles tiles = gameTilesBuilder.createInstance(json.getJSONObject("game_tiles"));
		Board board = boardBuilder.createInstance(json.getJSONObject("game_board"));
		
		List<String> usedWords = wordsBuilder.createInstance(json.getJSONObject("used_words"));
		
		game.reset(currentTurn, numConsecutivePassedTurns, wordsInBoard, gameFinished, 
				players, tiles, board, usedWords);
		
		return game;

	}
		
	/* Método createPlayers:
	 * Devuelve una instancia de la clase GamePlayers, creada por el builder de la clase GamePlayersBuilder.
	 */
	public static GamePlayers createPlayers(JSONObject data) {
		return gamePlayersBuilder.createInstance(data);
	}

	/* Método loadWordList:
	 * Devuelve, mediante la clase WordsBuilder, la lista formada por todas las palabras válidas del juego.
	 */
	public static List<String> loadWordList() throws JSONException, FileNotFoundException {
		
		JSONObject jo = new JSONObject(new JSONTokener(new FileInputStream(wordsFile)));
		return wordsBuilder.createInstance(jo);
	}
	
	/* Método initBuilders:
	 * Inicializa todos los builders necesarios para cargar o crear una partida.
	 * Se llama estáticamente desde el main.
	 */
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
}
