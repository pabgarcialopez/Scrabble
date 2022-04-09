package gameObjects;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import gameLogic.Game;

/* APUNTES GENERALES:
   
   Ver apuntes de la clase padre Player.
   
   Cabe señalar el atributo estático numEasyPlayers, que se emplea para llevar la cuenta
   de cuántos jugadores automáticos de este tipo se tienen en el juego, y así poder nombrarlos
   de manera distinta acorde a este número.
 */
public class EasyPlayer extends Player {

	private static int numEasyPlayers = 0;
	private static final int FORMED_WORDS_LENGTH = 2;
	
	public EasyPlayer(String name, int totalPoints, List<Tile> tiles) {
		super(name + " Easy " + ++numEasyPlayers, totalPoints, tiles);
	}

	/* Sobrescritura del método play:
	 * La estrategia del EasyPlayer es intentar colocar palabras de tamaño
	 * FORMED_WORDS_LENGTH. Si no consigue colocar palabra, y no puede 
	 * intercambiar ficha, pasa de turno.
	 */
	@Override
	public void play(Game game) {
		
		boolean wordWritten = false;
		
		List<Tile> tilesForWord = new ArrayList<Tile>(this.tiles);
		
		wordWritten = tryWritingInBoard(FORMED_WORDS_LENGTH, tilesForWord, game);
		
		if(!wordWritten && !game.swapTile()) {
			//if((int) (this.rdm.nextDouble() * 4) == 0) // 25% de probabilidad de pasar.
				game.passTurn();
		}		
	}

	@Override
	public boolean isHuman() {
		return false;
	}
	
	@Override
	public void reset() {
		--numEasyPlayers;
	}
	
	@Override
	public JSONObject report() {
		
		JSONObject jo = super.report();
		jo.put("type", "easy_player");
		jo.put("name", name.substring(0, name.lastIndexOf(" Easy")));
		
		return jo;
	}
}
