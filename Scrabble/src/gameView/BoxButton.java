package gameView;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import exceptions.CommandExecuteException;
import gameLogic.Game;
import gameObjects.Box;
import scrabble.Controller;

public class BoxButton extends JButton implements ScrabbleObserver {
	
	private static final long serialVersionUID = 1L;
	
	private int x;
	private int y;
	private Box box;
	private ChooseWordDialog chooseWordDialog;
	
	private Controller controller;

	BoxButton(Controller controller, int x, int y, ChooseWordDialog chooseWordDialog) {
		this.x = x;
		this.y = y;
		this.box = null;
		this.chooseWordDialog = chooseWordDialog;
		this.controller = controller;
		
		initGUI();
		this.controller.addObserver(this);
	}
	
	private void initGUI() {
		
		setToolTipText(String.format("Casilla (%s, %s)", this.x, this.y));
		setIcon(new ImageIcon("box_default_icon"));
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		this.box = new Box(null);
		
		addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(box != null && box.getTile() == null) {
					int status = chooseWordDialog.open(x, y);
					if(status == 1) {
						String word = chooseWordDialog.getSelectedWord();
						String direction = chooseWordDialog.getSelectedDirection();
						
						try {
							controller.writeAWord(word, x, y, direction);
						}
						
						catch (CommandExecuteException cee) {
							JOptionPane.showMessageDialog(BoxButton.this, cee.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			}
		});	
	}

	@Override
	public void onWordWritten(Game game) {
		if(this.box != game.getBoxAt(this.x, this.y)) {
			this.box = game.getBoxAt(this.x, this.y);
			setImage();
		}
	}

	@Override
	public void onRegister(Game game) {
		this.box = game.getBoxAt(this.x, this.y);
		setImage();
	}

	@Override
	public void onReset(Game game) {
		this.box = game.getBoxAt(this.x, this.y);
		setImage();
	}
	
	private void setImage() {
		
		if(box.getTile() != null)
			this.setIcon(new ImageIcon("letters/" + box.getTile().getLetter().toUpperCase() + ".png"));
		else if(box.getSpecialEffect() != null)
			this.setIcon(new ImageIcon("special_effects/" + box.getSpecialEffect() + ".png"));
	}
}
