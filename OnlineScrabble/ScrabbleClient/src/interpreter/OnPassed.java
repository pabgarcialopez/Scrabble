package interpreter;

import java.util.List;

import org.json.JSONObject;

import view.ScrabbleObserver;

public class OnPassed  extends OnAction {

	private final String type = "passed";
	
	private int numPlayers;
	
	private String currentPlayerName;
	
	@Override
	OnAction interpret(JSONObject jo) {
		
		if(this.type.equals(jo.getString("type"))) {
			
			JSONObject data = jo.getJSONObject("data");
			
			this.numPlayers = data.getInt("num_players");
			this.currentPlayerName = data.getString("current_player_name");
			
			return this;
		}
		
		return null;
	}

	@Override
	public void executeAction(List<ScrabbleObserver> observers) {

		for(ScrabbleObserver o : observers)
			o.onPassed(numPlayers, currentPlayerName);
	}

}
