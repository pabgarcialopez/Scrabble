package gameView;

import java.awt.Dimension;
import java.awt.FlowLayout;

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
	private JLabel infoLabel;
	private JLabel pointsLabel;

	InfoPanel(Controller controller) {
		
		initGUI();
		controller.addObserver(this);
	}
	
	private void initGUI() {
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		this.add(Box.createRigidArea(new Dimension(10, 30)));

		currentTurnLabel = new JLabel("Turno del jugador: ");
		add(currentTurnLabel);
		
		this.add(Box.createRigidArea(new Dimension(200, 30)));
		
		infoLabel = new JLabel("¡Bienvenido al Scrabble!");
		add(infoLabel);
		
		pointsLabel = new JLabel();
	}
	
	@Override
	public void onWordWritten(Game game, String word, int posX, int posY, String direction, int points, int extraPoints) {
		infoLabel.setText(String.format("El jugador %s escribe la palabra \"%s\" en la posición (%s, %s) y dirección \"%s\"", this.currentTurnName, word.toUpperCase(), posX, posY, direction.toUpperCase()));
		
		String pointsString = String.format("¡Gana %s puntos!", points);
		if(extraPoints != 0)
			pointsString += String.format(" Además, ¡gana %s puntos extra!", extraPoints);
		pointsLabel.setText(pointsString);
	}

	@Override
	public void onPassed(Game game) {
		infoLabel.setText(String.format("El jugador %s pasa de turno", this.currentTurnName));
	}

	@Override
	public void onSwapped(Game game) {
		infoLabel.setText(String.format("El jugador %s intercambia una ficha", this.currentTurnName));
	}

	@Override
	public void onRegister(Game game) {}

	@Override
	public void onReset(Game game) {
		this.currentTurnName = game.getPlayers().getPlayerName(game.getCurrentTurn());
		currentTurnLabel.setText("Turno del jugador: " + this.currentTurnName);
		infoLabel.setText("Nueva partida iniciada");
	}

	@Override
	public void onError(String error) {
		JOptionPane.showMessageDialog(this, error, "ERROR", JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void onUpdate(Game game) {
		this.currentTurnName = game.getPlayers().getPlayerName(game.getCurrentTurn());
		this.currentTurnLabel.setText("Turno del jugador: " + this.currentTurnName);
		
		this.infoLabel.setText("");
		
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
		
		JOptionPane.showMessageDialog(this, buffer.toString(), "Elección de Turnos", JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	public void onPlayersNotAdded(Game game) {}
}
