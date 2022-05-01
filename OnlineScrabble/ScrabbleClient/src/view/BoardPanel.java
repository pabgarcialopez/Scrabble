package view;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JPanel;

import containers.Board;
import containers.GamePlayers;
import control.Controller;

public class BoardPanel extends JPanel implements ScrabbleObserver {
	
	private static final long serialVersionUID = 1L;
	
	private Controller controller;
	private int clientNumPlayer;
	
	private ChooseWordDialog chooseWordDialog;

	BoardPanel(Controller controller, int clientNumPlayer) {
		
		this.controller = controller;
		this.clientNumPlayer = clientNumPlayer;
		
		this.chooseWordDialog = new ChooseWordDialog(this);
		
		this.setPreferredSize(new Dimension(730, 730));
		
		setVisible(true);
		
		this.controller.addObserver(this);
	}

	@Override
	public void onWordWritten(String word, int posX, int posY, String direction, int points, int extraPoints, int numPlayers, GamePlayers gamePlayers, int currentTurn, Board board) {}

	@Override
	public void onRegister(Board board, int numPlayers, GamePlayers gamePlayers, int currentTurn) {}

	@Override
	public void onReset(Board board, int numPlayers, String currentPlayerName, int remainingTiles, GamePlayers gamePlayers, int currentTurn) {
		
		this.removeAll();
		
		this.setLayout(new GridLayout(board.getBoardSize(), board.getBoardSize()));
		for(int i = 0; i < board.getBoardSize(); ++i)
			for(int j = 0; j < board.getBoardSize(); ++j) {
				this.add(new BoxButton(this.controller, i, j, this.chooseWordDialog, clientNumPlayer));
			}
		setPreferredSize(new Dimension(730, 730));
	}

	@Override
	public void onPassed(int numPlayers, String currentPlayerName) {}

	@Override
	public void onSwapped(int numPlayers, GamePlayers gamePlayers, int currentTurn) {}

	@Override
	public void onError(String error) {}

	@Override
	public void onUpdate(boolean gameFinished, int numPlayers, int remainingTiles, String currentPlayerName, GamePlayers gamePlayers, int currentTurn) {}

	@Override
	public void onEnd(String message) {}

	@Override
	public void onFirstTurnDecided(List<String> lettersObtained, GamePlayers gamePlayers, int numPlayers, int currentTurn) {}

	@Override
	public void onMovementNeeded(int currentTurn) {}
}
