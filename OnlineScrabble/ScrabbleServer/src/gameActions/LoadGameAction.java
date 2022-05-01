package gameActions;

import java.io.FileNotFoundException;

import org.json.JSONObject;

import logic.Game;
import storage.GameLoader;

public class LoadGameAction extends GameAction {

	private String file;
	
	LoadGameAction() {
		super("load_game");
	}

	@Override
	GameAction interpret(JSONObject jo) {
		
		if(this.type.equals(jo.getString("type"))) {
			
			JSONObject data = jo.getJSONObject("data");
			
			this.file = data.getString("file");
			
			return this;
		}
		
		return null;
	}

	@Override
	public void executeAction(Game game) throws FileNotFoundException {
		GameLoader.loadGame(game, file);
	}

}
