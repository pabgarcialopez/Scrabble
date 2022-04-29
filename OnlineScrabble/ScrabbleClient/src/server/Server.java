package server;

import java.io.FileNotFoundException;

import org.json.JSONObject;

import client.Client;
import gameActions.GameAction;
import logic.Game;
import logic.GameSerializer;

public class Server {
	
	private Game game;
	
	private Client client;

	public Server() {
		this.game = new Game(this);
	}
	
	public void doGameAction(JSONObject jo) {
		
		GameAction gameAction = GameAction.getAction(jo);
		
		try {
			gameAction.executeAction(game);
		} catch (FileNotFoundException e) {
			client.doViewAction(GameSerializer.serializeError(e.getMessage()));
		}
	}
	
	public void sendViewAction(JSONObject jo) {
		this.client.doViewAction(jo);
	}

	public JSONObject getRegistration() {
		return game.getRegistration();
	}
	
	public void setClient(Client client) {
		this.client = client;
	}
}
