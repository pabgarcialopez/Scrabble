package GameContainers;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import GameLogic.Game;
import GameObjects.Player;
import GameObjects.Tile;

public class GamePlayers {
	
	private static final int NUM_TILES = 7;
	
	private List <Player> players;
	
	public GamePlayers() {
		this.players = new ArrayList<Player>();
	}

	public void addPlayer(Player player) {
		
		// checkPlayerNames devuelve true si son diferentes los nombres.
		if(!checkPlayerNames(player.getName()))
			throw new IllegalArgumentException("Ya hay un jugador con el nombre " + player.getName());
			
		players.add(player);
	}
	
	public int getNumPlayers() {
		return players.size();
	}
	
	private boolean checkPlayerNames(String name) {
		boolean namesAreDifferent = true;
		
		int i = 0;
		while(namesAreDifferent && i < players.size()) {
			if(players.get(i).getName().equals(name))
				namesAreDifferent = false;
			
			++i;
		}
		
		return namesAreDifferent;
	}
	
	public void drawTiles(Game game, int i) {
		
		for(int j = players.get(i).getNumTiles(); j < NUM_TILES; j++) {
			
			Tile tile = game.getRandomTile();
			
			if(tile != null) {
				players.get(i).addTile(tile);
				game.removeTile(tile);
			}
			
			else break;
		}
	}

	public String getPlayerStatus(int i) {
		return players.get(i).getStatus();
	}
}
