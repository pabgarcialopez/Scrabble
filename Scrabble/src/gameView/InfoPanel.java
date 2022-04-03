package gameView;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import gameLogic.Game;
import gameUtils.StringUtils;
import scrabble.Controller;

public class InfoPanel extends JPanel implements ScrabbleObserver {

	private static final long serialVersionUID = 1L;
	
	private String currentTurnName;
	
	private JLabel currentTurnLabel;
	private JLabel remainingTilesLabel;
	private JLabel infoLabel;
	private JLabel pointsLabel;
	
	private Component parent;

	InfoPanel(Controller controller, Component parent) {
		
		this.parent = parent;
		initGUI();
		controller.addObserver(this);
	}
	
	private void initGUI() {
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		add(Box.createRigidArea(new Dimension(10, 30)));
		
		JPanel turnAndTilesPanel = new JPanel();
		add(turnAndTilesPanel);
		turnAndTilesPanel.setLayout(new BoxLayout(turnAndTilesPanel, BoxLayout.Y_AXIS));

		currentTurnLabel = new JLabel("");
		turnAndTilesPanel.add(currentTurnLabel);
		
		turnAndTilesPanel.add(Box.createRigidArea(new Dimension(1, 5)));
		
		remainingTilesLabel = new JLabel("");
		turnAndTilesPanel.add(remainingTilesLabel);
		
		add(Box.createRigidArea(new Dimension(30, 30)));
		
		JPanel infoPanel = new JPanel();
		add(infoPanel);
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
		
		infoLabel = new JLabel("¡Bienvenido al Scrabble!");
		infoPanel.add(infoLabel);
		
		infoPanel.add(Box.createRigidArea(new Dimension(1, 5)));
		
		pointsLabel = new JLabel();
		infoPanel.add(pointsLabel);
	}
	
	@Override
	public void onWordWritten(Game game, String word, int posX, int posY, String direction, int points, int extraPoints) {
		infoLabel.setText(String.format("%s escribe la palabra \"%s\" en la posición (%s, %s) y dirección \"%s\"", this.currentTurnName, word.toUpperCase(), posX, posY, direction.toUpperCase()));
		
		String pointsString = String.format("¡Gana %s puntos!", points);
		if(extraPoints != 0)
			pointsString += String.format(" Además, ¡gana %s puntos extra!", extraPoints);
		pointsLabel.setText(pointsString);
	}

	@Override
	public void onPassed(Game game) {
		infoLabel.setText(String.format("%s pasa de turno", this.currentTurnName));
	}

	@Override
	public void onSwapped(Game game) {
		infoLabel.setText(String.format("%s intercambia una ficha", this.currentTurnName));
	}

	@Override
	public void onRegister(Game game) {}

	@Override
	public void onReset(Game game) {
		this.currentTurnName = game.getPlayers().getPlayerName(game.getCurrentTurn());
		currentTurnLabel.setText("Turno de: " + this.currentTurnName);
		infoLabel.setText("Nueva partida iniciada");
		remainingTilesLabel.setText("Fichas restantes: " + game.getRemainingTiles());
	}

	@Override
	public void onError(String error) {
		JOptionPane.showMessageDialog(parent, error, "ERROR", JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void onUpdate(Game game) {
		this.currentTurnName = game.getPlayers().getPlayerName(game.getCurrentTurn());
		this.currentTurnLabel.setText("Turno de: " + this.currentTurnName);
		remainingTilesLabel.setText("Fichas restantes: " + game.getRemainingTiles());
		
		if(!game.humanIsPlaying()) this.infoLabel.setText("Eligiendo movimiento...");
		else this.infoLabel.setText("Elige tu siguiente movimiento");
		
		this.pointsLabel.setText("");
	}

	@Override
	public void onEnd() {
		JOptionPane.showMessageDialog(this, "¡Gracias por jugar!", "Scrabble", JOptionPane.INFORMATION_MESSAGE);
		System.exit(0);
	}

	@Override
	public void onFirstTurnDecided(Game game, String[] lettersObtained) {
		
		StringBuilder buffer = new StringBuilder();
		
		for(int i = 0; i < game.getPlayers().getNumPlayers(); i++) {
			buffer.append(game.getPlayers().getPlayerName(i)).append(" ha cogido una ")
				  .append(lettersObtained[i]).append(StringUtils.LINE_SEPARATOR);
		}
		
		buffer.append(StringUtils.LINE_SEPARATOR).append("El orden de juego es: ");
		
		for(int i = 0; i < game.getPlayers().getNumPlayers(); i++) {
			buffer.append(game.getPlayers().getPlayerName((i + game.getCurrentTurn()) % game.getPlayers().getNumPlayers()));
			if(i != game.getPlayers().getNumPlayers() - 1) buffer.append(" -> ");
		}
			
		
		buffer.append(StringUtils.LINE_SEPARATOR);
		
		JOptionPane.showMessageDialog(parent, buffer.toString(), "Elección de Turnos", JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	public void onPlayersNotAdded(Game game) {}
}
