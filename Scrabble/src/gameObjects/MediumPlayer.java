package gameObjects;

import java.util.ArrayList;
import java.util.List;

import gameLogic.Game;

public class MediumPlayer extends Player {
	
	private static int numMediumPlayers = 0;

	public MediumPlayer(String name, int totalPoints, List<Tile> tiles) {
		super(name + " Medium " + ++numMediumPlayers, totalPoints, tiles);
	}

	@Override
	public void play(Game game) {
		
		boolean played = false;
		
		List<Tile> tilesForWord = new ArrayList<Tile>(this.tiles);
		
		if(!game.getWordsInBoard()) {
			for(int wordLength = tilesForWord.size(); wordLength > 1 && !played; --wordLength)
				played = tryWritingInBoardWithoutWords(wordLength, tilesForWord, game);
		}
		else {
			for(int wordLength = tilesForWord.size() + 1; wordLength > 1 && !played; --wordLength)
				played = tryWritingInBoardWithWords(wordLength, tilesForWord, game);
		}
		
		
		if(!played) {
			if(game.swapTile()) {
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
}
