package gameView;

import gameLogic.Game;

public interface ScrabbleObserver {

	void onWordWritten(Game game, String word, int posX, int posY, String direction, int points, int extraPoints);
	
	void onPassed(Game game);
	
	void onSwapped(Game game);
	
	void onRegister(Game game);
	
	void onReset(Game game);
	
	void onError(String error);
	
	void onUpdate(Game game);
	
	void onEnd();
	
	void onFirstTurnDecided(Game game, String[] lettersObtained);
	
	void onPlayersNotAdded(Game game);
}
