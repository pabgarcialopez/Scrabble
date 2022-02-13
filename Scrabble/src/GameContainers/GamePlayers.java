package GameContainers;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import GameObjects.Player;

public class GamePlayers {
	
	private List <Player> players;
	
	public GamePlayers() {
		this.players = new ArrayList<Player>();
	}

	public void addPlayer(Player player) {
		
		// True si son diferentes los nombres.
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
	
	
	
	
	
	

}
