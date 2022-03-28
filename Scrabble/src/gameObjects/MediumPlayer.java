package gameObjects;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONObject;

import gameLogic.Game;

public class MediumPlayer extends Player {
	
	private static int numMediumPlayers = 0;
	private Random rdm;

	public MediumPlayer(String name, int totalPoints, List<Tile> tiles) {
		super(name + " Medium " + ++numMediumPlayers, totalPoints, tiles);
		this.rdm = new Random();
	}

	@Override
	public void play(Game game) {
		
		boolean played = false;
		
		List<Integer> lengths = new ArrayList<Integer>();
		for(int i = 2; i <= this.getNumTiles(); ++i)
			lengths.add(i);
		
		List<Tile> tilesForWord = new ArrayList<Tile>(this.tiles);
		
		if(!game.getWordsInBoard()) {
			while(!played && lengths.size() > 0) {
				int i = (int) (this.rdm.nextDouble() * lengths.size());
				played = this.tryWritingInBoardWithoutWords(lengths.remove(i), tilesForWord, game);
			}
		}
		else {
			lengths.add(this.getNumTiles() + 1);
			while(!played && lengths.size() > 0) {
				int i = (int) (this.rdm.nextDouble() * lengths.size());
				played = this.tryWritingInBoardWithWords(lengths.remove(i), tilesForWord, game);
			}
		}
		
		if(!played) {
			int i = (int) (this.rdm.nextDouble() * 2);
			if(i == 0 && game.swapTile()) {
				System.out.println(String.format("El jugador %s intercambia una ficha.%n", this.name));
			}
			else {
				game.passTurn();
				System.out.println(String.format("El jugador %s pasa de turno.%n", this.name));
			}
		}
	}

	@Override
	public boolean isHuman() {
		return false;
	}
	
	@Override
	public JSONObject report() {
		
		JSONObject jo = super.report();
		jo.put("type", "medium_player");
		
		return jo;
	}
}
