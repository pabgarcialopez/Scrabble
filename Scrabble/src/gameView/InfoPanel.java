package gameView;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import gameLogic.Game;
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
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		currentTurnLabel = new JLabel("Turno del jugador: ");
		add(currentTurnLabel);
		
		infoLabel = new JLabel();
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
	public void onRegister(Game game) {
		this.currentTurnName = game.getPlayers().getPlayerName(game.getCurrentTurn());
		currentTurnLabel.setText("Turno del jugador: " + this.currentTurnName);
	}

	@Override
	public void onReset(Game game) {
		currentTurnLabel.setText("Turno del jugador: " + this.currentTurnName);
		infoLabel.setText("Se acaba de resetear el juego");
	}

	@Override
	public void onError(String error) {
		JOptionPane.showMessageDialog(this, error, "ERROR", JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void onUpdate(Game game) {
		this.currentTurnName = game.getPlayers().getPlayerName(game.getCurrentTurn());
	}

	@Override
	public void onEnd() {
		JOptionPane.showMessageDialog(this, "¡Gracias por jugar!", "Scrabble", JOptionPane.INFORMATION_MESSAGE);
		System.exit(0);
	}
}
