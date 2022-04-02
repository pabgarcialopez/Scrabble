package storage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
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

public class GameLoader {
	
	private static final String wordsFile = "words.json";
	public static final String NEW_GAME = "partidas/new_game.json";
	
	private static BoardBuilder boardBuilder;
	private static GameTilesBuilder gameTilesBuilder;
	private static GamePlayersBuilder gamePlayersBuilder;
	private static WordsBuilder wordsBuilder = new WordsBuilder();
	
	public static Game newGame(Game game) throws FileNotFoundException {
		return createGame(new FileInputStream(NEW_GAME), game);
	}
	
	public static Game loadGame(Game game, String file) throws FileNotFoundException {
		
		if(!file.endsWith(".json"))
			file += ".json";
		
		return createGame(new FileInputStream(file), game);
	}
	
	private static Game createGame(InputStream input, Game game) {
		
		JSONObject json = new JSONObject(new JSONTokener(input));
		
		int currentTurn = json.getInt("current_turn"); // -1 si es partida nueva
		int numConsecutivePassedTurns = json.getInt("consecutive_turns_passed");
		boolean wordsInBoard = json.getBoolean("words_in_board");
		boolean gameFinished = json.getBoolean("game_finished");
		
		GamePlayers players = gamePlayersBuilder.createInstance(json.getJSONObject("game_players"));
		
		GameTiles tiles = gameTilesBuilder.createInstance(json.getJSONObject("game_tiles"));
		Board board = boardBuilder.createInstance(json.getJSONObject("game_board"));
		
		List<String> usedWords = wordsBuilder.createInstance(json.getJSONObject("used_words"));
		
		
		// Creo que este game nunca va a ser nulo.
//		if(game == null)
//			return new Game(currentTurn, numConsecutivePassedTurns, wordsInBoard, gameFinished, 
//				players, tiles, board, usedWords);
//		
//		else {
			game.reset(currentTurn, numConsecutivePassedTurns, wordsInBoard, gameFinished, 
					players, tiles, board, usedWords);
			
			return game;
//		}
	}
		
	public static GamePlayers createPlayers(JSONObject data) {
		return gamePlayersBuilder.createInstance(data);
	}

	public static List<String> loadWordList() throws JSONException, FileNotFoundException {
		
		JSONObject jo = new JSONObject(new JSONTokener(new FileInputStream(wordsFile)));
		return wordsBuilder.createInstance(jo);
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
}
