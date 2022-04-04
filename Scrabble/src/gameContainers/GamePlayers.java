package gameContainers;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import gameLogic.Game;
import gameObjects.Player;
import gameObjects.Tile;

public class GamePlayers {
	
	public static final int NUM_TILES = 7;
	
	private List <Player> players;
	
	// Constructor para nueva partida.
	//public GamePlayers() {
	//	this.players = new ArrayList<Player>();
	//}
	
	// Constructor para la carga de partida.
	public GamePlayers(List<Player> players) {
		this.players = players;
	}
	
	public void drawTiles(Game game, int i) {
		
		for(int j = players.get(i).getNumTiles(); j < NUM_TILES; j++) {
			
			Tile tile = game.getRandomTile();
			
			if(tile != null) {
				players.get(i).addTile(tile);
				game.removeTile(tile);
			}
			
			else break; // Si tile == null, significa que no quedan en el saco de fichas y no hay que continuar el bucle.
		}
	}
	
	public void removePlayerTile(int player, Tile tile) {
		this.players.get(player).removeTile(tile);		
	}

	public int numberOfTilesOf(int player, String letter) {
		return this.players.get(player).numberOfTilesOf(letter);
	}
	
	public void givePoints(int player, int points) {
		this.players.get(player).givePoints(points);
	}
	
	public void removePlayerTile(int player, int tile) {
		this.players.get(player).removeTile(tile);		
	}

	public boolean playerHasLetter(int player, String letter) {
		return this.players.get(player).hasLetter(letter);
	}
	
	public int getNumPlayers() {
		return players.size();
	}

	public String getPlayerStatus(int i) {
		return players.get(i).getStatus();
	}

	public String getPlayerName(int i) {
		return players.get(i).getName();
	}

	public int getNumPlayerTiles(int i) {
		return this.players.get(i).getNumTiles();
	}

	public Tile getPlayerTile(int player, int tile) {
		return this.players.get(player).getTile(tile);
	}
	
	public Tile getPlayerTile(int player, String letter) {
		return this.players.get(player).getTile(letter);
	}
	
	public JSONObject report() {
		
		JSONObject jo = new JSONObject();
		JSONArray players = new JSONArray();
		
		for(int i = 0; i < this.players.size(); ++i)
			players.put(this.players.get(i).report());
		
		jo.put("players", players);
		
		return jo;
	}

	public boolean humanIsPlaying(int currentTurn) {
		return players.get(currentTurn).isHuman();
	}

	public void automaticPlay(int player, Game game) {
		this.players.get(player).play(game);
	}

	public int getPlayerPoints(int player) {
		
		return this.players.get(player).getPoints();
	}

	public List<Integer> getWinners() {
		List<Integer> winners = new ArrayList<Integer>();
		for(int i = 0; i < this.getNumPlayers(); ++i) {
			if(winners.size() == 0)
				winners.add(i);
			else {
				if(this.players.get(winners.get(0)).getPoints() == this.players.get(i).getPoints())
					winners.add(i);
				else if (this.players.get(winners.get(0)).getPoints() < this.players.get(i).getPoints()) {
					winners.clear();
					winners.add(i);
				}
			}
		}
		
		return winners;
	}
}
