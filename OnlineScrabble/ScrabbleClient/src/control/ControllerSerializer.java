package control;

import org.json.JSONObject;

import containers.GamePlayers;

public class ControllerSerializer {

	public static JSONObject writeAWord(String word, int posX, int posY, String direction) {
		
		JSONObject jo = new JSONObject();
		JSONObject data = new JSONObject();
		
		data.put("word", word);
		data.put("pos_X", posX);
		data.put("pos_Y", posY);
		data.put("direction", direction);
		
		jo.put("data", data);
		jo.put("type", "write_a_word");
		
		return jo;
	}

	public static JSONObject passTurn() {
		
		JSONObject jo = new JSONObject();
		jo.put("type", "pass_turn");
		
		return jo;
	}

	public static JSONObject swapTile() {

		JSONObject jo = new JSONObject();
		jo.put("type", "swap_tile");
		
		return jo;
	}

	public static JSONObject userExits() {
		
		JSONObject jo = new JSONObject();
		jo.put("type", "user_exits");
		
		return jo;
	}

	public static JSONObject update() {
		
		JSONObject jo = new JSONObject();
		jo.put("type", "update");
		
		return jo;
	}

	public static JSONObject newGame() {
		
		JSONObject jo = new JSONObject();
		jo.put("type", "new_game");
		
		return jo;
	}

	public static JSONObject loadGame(String file) {
		
		JSONObject jo = new JSONObject();
		JSONObject data = new JSONObject();
		
		data.put("file", file);
		jo.put("type", "load_game");
		jo.put("data", data);
		
		return jo;
	}

	public static JSONObject saveGame(String file) {
		
		JSONObject jo = new JSONObject();
		JSONObject data = new JSONObject();
		
		data.put("file", file);
		jo.put("type", "save_game");
		jo.put("data", data);
		
		return jo;
	}

	public static JSONObject addOrChangePlayers(GamePlayers players) {
		JSONObject jo = new JSONObject();
		JSONObject data = new JSONObject();
		
		data.put("game_players", players.report());
		jo.put("type", "add_or_change_players");
		jo.put("data", data);
		
		return jo;
	}
}
