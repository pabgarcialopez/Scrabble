package factories;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import gameContainers.GamePlayers;
import gameObjects.Player;

public class GamePlayersBuilder extends Builder<GamePlayers> {

	private PlayerBuilder playerBuilder;
	
	public GamePlayersBuilder(PlayerBuilder playerBuilder) {
		super("gamePlayers");
	}

	@Override
	protected GamePlayers createTheInstance(JSONObject data) {
		
		JSONArray jsonArrayPlayers = data.getJSONArray("players");
		
		List<Player> players = new ArrayList<Player>();
		
		for(int i = 0; i < jsonArrayPlayers.length(); i++)
			players.add(playerBuilder.createTheInstance(jsonArrayPlayers.getJSONObject(i)));
		
		return new GamePlayers(players);
		
	}

}
