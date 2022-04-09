package gameObjects;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import gameLogic.Game;

/* APUNTES GENERALES:

   Ver apuntes de la clase padre Player.

   Cabe señalar el atributo estático numMediumPlayers, que se emplea para llevar la cuenta
   de cuántos jugadores automáticos de este tipo se tienen en el juego, y así poder nombrarlos
   de manera distinta acorde a este número.
*/
public class MediumPlayer extends Player {
	
	private static int numMediumPlayers = 0;

	public MediumPlayer(String name, int totalPoints, List<Tile> tiles) {
		super(name + " Medium " + ++numMediumPlayers, totalPoints, tiles);
	}

	/* Sobrescritura del método play:
	 * La estrategia de MediumPlayer es intentar colocar palabras de tamaño
	 * entre 2 y el número de sus fichas (+1, si ya hay palabras en el tablero).
	 * A diferencia del HardPlayer, el tamaño de las palabras que intenta colocar
	 * se genera de manera aleatoria (aunque no se repiten tamaños).
	 * Si no ha conseguido escribir una palabra, y tampoco ha intercambiado una ficha,
	 * tiene un 50% de probabilidades de pasar turno.
	 */
	@Override
	public void play(Game game) {
		
		boolean wordWritten = false;
		
		List<Integer> lengths = new ArrayList<Integer>();
		for(int i = 2; i <= this.getNumTiles(); ++i)
			lengths.add(i);
		
		List<Tile> tilesForWord = new ArrayList<Tile>(this.tiles);
		
		if(game.getWordsInBoard())
			lengths.add(this.getNumTiles() + 1);

		while(!wordWritten && lengths.size() > 0) {
			int i = (int) (this.rdm.nextDouble() * lengths.size());
			wordWritten = tryWritingInBoard(lengths.remove(i), tilesForWord, game);
		}
		
		if(!wordWritten) {
			int i = (int) (this.rdm.nextDouble() * 2);
			if(i == 0 && !game.swapTile())
				game.passTurn();
		}
	}

	@Override
	public boolean isHuman() {
		return false;
	}
	
	@Override
	public void reset() {
		--numMediumPlayers;
	}
	
	@Override
	public JSONObject report() {
		
		JSONObject jo = super.report();
		jo.put("type", "medium_player");
		
		jo.put("name", name.substring(0, name.lastIndexOf(" Medium")));
		
		return jo;
	}
}
