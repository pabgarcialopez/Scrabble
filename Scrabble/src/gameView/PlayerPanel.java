package gameView;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import gameLogic.Game;
import scrabble.Controller;

public class PlayerPanel extends JPanel implements ScrabbleObserver {

	
	private static final long serialVersionUID = 1L;
	
	private JPanel tilesPanel;
	
	private JLabel nameLabel;
	
	private JLabel pointsLabel;
	
	private int numJugador;

	public PlayerPanel(Controller controller, int numJugador) {
		
		this.numJugador = numJugador;
		
		initGUI();
		
		controller.addObserver(this);
	}
	
	private void initGUI() {
		
		setAlignmentX(CENTER_ALIGNMENT);
		setAlignmentY(CENTER_ALIGNMENT);
		
		this.tilesPanel = new JPanel();
		if(numJugador == 1 || numJugador == 3) {
			add(Box.createRigidArea(new Dimension(20, 120)));
			this.tilesPanel.setLayout(new BoxLayout(this.tilesPanel, BoxLayout.Y_AXIS));
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			this.tilesPanel.setPreferredSize(new Dimension(45, 315));
		}
		else {
			add(Box.createRigidArea(new Dimension(1, 70)));
			this.tilesPanel.setLayout(new BoxLayout(this.tilesPanel, BoxLayout.X_AXIS));
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			this.tilesPanel.setPreferredSize(new Dimension(315, 45));
		}
		
		JPanel nameAndPointsPanel = new JPanel();
		nameAndPointsPanel.setLayout(new BoxLayout(nameAndPointsPanel, BoxLayout.Y_AXIS));
		
		this.nameLabel = new JLabel();
		this.pointsLabel = new JLabel();
		
		nameAndPointsPanel.add(this.nameLabel);
		nameAndPointsPanel.add(this.pointsLabel);
		
		if(this.numJugador == 0 || this.numJugador == 3) {
			add(nameAndPointsPanel);
			add(Box.createRigidArea(new Dimension(50, 50)));			
		}
			
		add(this.tilesPanel);
		
		if(this.numJugador == 1 || this.numJugador == 2) {
			add(Box.createRigidArea(new Dimension(50, 50)));			
			add(nameAndPointsPanel);
		}	
	}
	
	private void createTiles(Game game) {
		
		this.tilesPanel.removeAll();
		
		if(game.getCurrentTurn() == this.numJugador) {
			for(int i = 0; i < game.getPlayers().getNumPlayerTiles(this.numJugador); ++i) {
				JButton tileButton = new JButton();
				tileButton.setIcon(new ImageIcon("resources/icons/letters/" + game.getPlayers().getPlayerTile(this.numJugador, i).getLetter() + ".png"));
				tileButton.setPreferredSize(new Dimension(45, 45));
				tileButton.setMaximumSize(new Dimension(45, 45));
				tileButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
				this.tilesPanel.add(tileButton);
			}
		}
		else {
			for(int i = 0; i < game.getPlayers().getNumPlayerTiles(this.numJugador); ++i) {
				JButton tileButton = new JButton();
				tileButton.setIcon(new ImageIcon("resources/icons/letters/reversed_tile.png"));
				tileButton.setPreferredSize(new Dimension(45, 45));
				tileButton.setMaximumSize(new Dimension(45, 45));
				tileButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
				this.tilesPanel.add(tileButton);
			}
		}
	}
	
	@Override
	public void onWordWritten(Game game, String word, int posX, int posY, String direction, int points,
			int extraPoints) {
		if(game.getCurrentTurn() == this.numJugador) createTiles(game);
	}

	@Override
	public void onPassed(Game game) {}

	@Override
	public void onSwapped(Game game) {
		if(game.getCurrentTurn() == this.numJugador) createTiles(game);
	}

	@Override
	public void onRegister(Game game) {
		if(Game.getGameInitiated() && this.numJugador < game.getPlayers().getNumPlayers()) {
			this.nameLabel.setText("Jugador: " + game.getPlayers().getPlayerName(this.numJugador));
			this.pointsLabel.setText("Puntos totales: " + game.getPlayers().getPlayerPoints(this.numJugador));
			createTiles(game);
		}
		else {
			this.nameLabel.setText("");
			this.pointsLabel.setText("");
			this.tilesPanel.removeAll();
		}
	}

	@Override
	public void onReset(Game game) {
		if(Game.getGameInitiated() && this.numJugador < game.getPlayers().getNumPlayers()) {
			this.nameLabel.setText("Jugador: " + game.getPlayers().getPlayerName(this.numJugador));
			this.pointsLabel.setText("Puntos totales: " + game.getPlayers().getPlayerPoints(this.numJugador));
			createTiles(game);
		}
		else {
			this.nameLabel.setText("");
			this.pointsLabel.setText("");
			this.tilesPanel.removeAll();
		}
	}

	@Override
	public void onError(String error) {}

	@Override
	public void onUpdate(Game game) {
		if(Game.getGameInitiated() && this.numJugador < game.getPlayers().getNumPlayers()) {
			this.nameLabel.setText("Jugador: " + game.getPlayers().getPlayerName(this.numJugador));
			this.pointsLabel.setText("Puntos totales: " + game.getPlayers().getPlayerPoints(this.numJugador));
			createTiles(game);
		}
		else {
			this.nameLabel.setText("");
			this.pointsLabel.setText("");
			this.tilesPanel.removeAll();
		}
	}

	@Override
	public void onEnd() {}

	@Override
	public void onFirstTurnDecided(Game game, String[] lettersObtained) {}

	@Override
	public void onPlayersNotAdded(Game game) {}
}
