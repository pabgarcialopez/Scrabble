package simulatedObjects;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import logic.Game;

/* APUNTES GENERALES:

   Ver apuntes de la clase padre Player.

   Cabe señalar el atributo estático numHardPlayers, que se emplea para llevar la cuenta
   de cuántos jugadores automáticos de este tipo se tienen en el juego, y así poder nombrarlos
   de manera distinta acorde a este número.
*/
public class HardPlayer extends Player{
	
	private static int numHardPlayers = 0;

	public HardPlayer(String name, int totalPoints, List<Tile> tiles) {
		super(name + " Hard " + ++numHardPlayers, totalPoints, tiles);
	}

	/* Sobrescritura del método play:
	 * La estrategia del HardPlayer es intentar colocar palabras del mayor
	 * tamaño posible. Dependiendo de si hay palabras en el tablero o no,
	 * empezará a intentar formar palabras de tamaño tiles.size(), o tiles.size() + 1.
	 * Si no consigue colocar palabra, y no puede intercambiar ficha, pasa de turno.
	 */

	@Override
	public void play(Game game) {
		
		boolean wordWritten = false;
		
		List<Tile> tilesForWord = new ArrayList<Tile>(this.tiles);
		
		int extraTile = (Game.getWordsInBoard() ? 1 : 0);
		for(int wordLength = tilesForWord.size() + extraTile; wordLength > 1 && !wordWritten; --wordLength)
			wordWritten = tryWritingInBoard(wordLength, tilesForWord, game);
		
		if(!wordWritten && !game.swapTile()) 
			game.passTurn();
		
	}

	@Override
	public boolean isHuman() {
		return false;
	}
	
	@Override
	public void reset() {
		--numHardPlayers;
	}
	
	@Override
	public JSONObject report() {
		
		JSONObject jo = super.report();
		jo.put("type", "hard_player");
		jo.put("name", name.substring(0, name.lastIndexOf(" Hard")));
		
		return jo;
	}
	
	

}
