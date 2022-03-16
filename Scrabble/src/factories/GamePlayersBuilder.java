package factories;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import gameContainers.GamePlayers;
import gameObjects.Player;

public class GamePlayersBuilder extends Builder<GamePlayers>{

	private PlayerBuilder playerBuilder;
	
	GamePlayersBuilder(PlayerBuilder playerBuilder) {
		super("gamePlayers");
	}

	@Override
	protected GamePlayers createTheInstance(JSONObject data) {
		
		if(data == null)
			return new GamePlayers();
		
		else {
			
			JSONArray jsonArrayPlayers = data.getJSONArray("players");
			
			List<Player> players = new ArrayList<Player>();
			
			for(int i = 0; i < jsonArrayPlayers.length(); i++)
				players.add(playerBuilder.createTheInstance(jsonArrayPlayers.getJSONObject(i)));
			
			return new GamePlayers(players);
		}
	}

}
