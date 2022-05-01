package gameActions;

import java.io.FileNotFoundException;

import org.json.JSONObject;

import logic.Game;
import storage.GameLoader;

public class NewGameAction extends GameAction {

	NewGameAction() {
		super("new_game");
	}

	@Override
	GameAction interpret(JSONObject jo) {

		if(this.type.equals(jo.getString("type")))
			return this;
		
		return null;
	}

	@Override
	public void executeAction(Game game) throws FileNotFoundException {
		GameLoader.newGame(game);
	}
}
