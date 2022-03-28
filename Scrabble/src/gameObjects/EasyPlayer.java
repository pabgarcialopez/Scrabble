package gameObjects;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONObject;

import gameLogic.Game;

public class EasyPlayer extends Player {

	private static int numEasyPlayers = 0;
	private Random rdm;
	
	public EasyPlayer(String name, int totalPoints, List<Tile> tiles) {
		super(name + " Easy " + ++numEasyPlayers, totalPoints, tiles);
		this.rdm = new Random();
	}

	@Override
	public void play(Game game) {
		
		boolean played = false;
		
		if(!game.getWordsInBoard())
			played = tryWritingInBoardWithoutWords(2, new ArrayList<Tile>(this.tiles), game);
		else
			played = tryWritingInBoardWithWords(2, new ArrayList<Tile>(this.tiles), game);
		
		
		if(!played) {
			int i = (int) (this.rdm.nextDouble() * 4);
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
		jo.put("type", "easy_player");
		
		return jo;
	}
}
