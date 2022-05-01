package server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import gameActions.GameAction;
import logic.Game;
import logic.GameSerializer;
import storage.GameLoader;

public class Server extends Thread {

	private Game game;

	private ServerSocket serverSocket;

	private int puerto;

	private List<ClientThread> clients;

	private int numPlayers;

	public Server(int puerto, int numPlayers) {
		this.game = new Game(this);
		this.puerto = puerto;
		this.clients = new ArrayList<ClientThread>();
		this.numPlayers = numPlayers;
		try {
			GameLoader.newGame(this.game);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void doGameAction(JSONObject jo) {
		
		GameAction gameAction = GameAction.getAction(jo);

		try {
			gameAction.executeAction(game);
		} catch (FileNotFoundException e) {
			this.sendViewAction(GameSerializer.serializeError(e.getMessage()));
		}
	}

	public void sendViewAction(JSONObject jo) {
		for(ClientThread ct : this.clients)
			ct.sendViewAction(jo);
	}

	@Override
	public void run() {

		try {
			this.serverSocket = new ServerSocket(this.puerto);
			
			while (this.clients.size() < this.numPlayers) {
				
				Socket socket;

				socket = serverSocket.accept();

				ClientThread client = new ClientThread(socket, this);
				client.start();
			}
			
			this.game.decideFirstTurn();
			
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}
