package gameView;

import gameLogic.Game;

public interface ScrabbleObserver {

	void onWordWritten(Game game);
	
	void onRegister(Game game);
	
	void onReset(Game game);
}
