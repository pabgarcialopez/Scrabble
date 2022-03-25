package factories;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import gameContainers.GamePlayers;
import gameObjects.Player;

public class GamePlayersBuilder extends Builder<GamePlayers> {

	private List<PlayerBuilder> playerBuilders;
	
	public GamePlayersBuilder(List<PlayerBuilder> playerBuilders) {
		super("gamePlayers");
		this.playerBuilders = playerBuilders;
	}

	@Override
	protected GamePlayers createTheInstance(JSONObject data) {
		
		JSONArray jsonArrayPlayers = data.getJSONArray("players");
		
		List<Player> players = new ArrayList<Player>();
		
		for(int i = 0; i < jsonArrayPlayers.length(); i++) {
			Player playerToAdd = null;
			for(PlayerBuilder pb : this.playerBuilders) {
				playerToAdd = pb.createTheInstance(jsonArrayPlayers.getJSONObject(i));
				if(playerToAdd != null) break;
			}
			
			if(playerToAdd != null)
				players.add(playerToAdd);
			else
				throw new InputMismatchException("El fichero JSON no es válido");
		}
		
		return new GamePlayers(players);
		
	}

}
