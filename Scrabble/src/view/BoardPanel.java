package view;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JPanel;

import logic.Game;
import scrabble.Controller;

public class BoardPanel extends JPanel implements ScrabbleObserver {
	
	private static final long serialVersionUID = 1L;
	
	private Controller controller;
	
	private ChooseWordDialog chooseWordDialog;

	BoardPanel(Controller controller) {
		
		this.controller = controller;
		
		this.chooseWordDialog = new ChooseWordDialog(this);
		
		this.setPreferredSize(new Dimension(730, 730));
		
		setVisible(true);
		
		this.controller.addObserver(this);
	}

	@Override
	public void onWordWritten(Game game, String word, int posX, int posY, String direction, int points, int extraPoints) {}

	@Override
	public void onRegister(Game game) {}

	@Override
	public void onReset(Game game) {
		
		this.removeAll();
		
		this.setLayout(new GridLayout(game.getBoardSize(), game.getBoardSize()));
		for(int i = 0; i < game.getBoardSize(); ++i)
			for(int j = 0; j < game.getBoardSize(); ++j) {
				this.add(new BoxButton(this.controller, i, j, this.chooseWordDialog));
			}
		setPreferredSize(new Dimension(730, 730));
	}

	@Override
	public void onPassed(Game game) {}

	@Override
	public void onSwapped(Game game) {}

	@Override
	public void onError(String error) {}

	@Override
	public void onUpdate(Game game) {}

	@Override
	public void onEnd(String message) {}

	@Override
	public void onFirstTurnDecided(Game game, String[] lettersObtained) {}

	@Override
	public void onMovementNeeded() {}
}
