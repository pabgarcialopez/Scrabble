package gameView;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import gameLogic.Game;
import scrabble.Controller;

public class ControlPanel extends JPanel implements ScrabbleObserver {

	private static final long serialVersionUID = 1L;
	
	private Controller controller;
	
	private JToolBar bar;
	
	private List<JButton> buttonsToBlockGameNotInitiated;
	
	private List<JButton> buttonsToBlockCPUTurn;
	
	ControlPanel(Controller controller) {
		
		this.controller = controller;
		
		this.buttonsToBlockCPUTurn = new ArrayList<JButton>();
		
		this.buttonsToBlockGameNotInitiated = new ArrayList<JButton>();
		
		initGUI();
		
		this.controller.addObserver(this);
	}
	
	private void initGUI() {
		
		this.bar = new JToolBar();
		this.add(bar);
		
		int barWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		this.bar.setPreferredSize(new Dimension(barWidth, 50));
		
		JButton newGameButton = new JButton();
		newGameButton.setActionCommand("newGame");
		newGameButton.setToolTipText("Iniciar una partida nueva");
		newGameButton.setIcon(new ImageIcon("resources/icons/new_game.png"));
		newGameButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					controller.newGame();
				}
				catch (FileNotFoundException fnfe) {
					JOptionPane.showMessageDialog(ControlPanel.this, "El fichero de nueva partida no es válido", "ERROR", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		bar.add(newGameButton);
		bar.addSeparator();
		
		JButton loadButton = new JButton();
		loadButton.setActionCommand("load");
		loadButton.setToolTipText("Cargar una partida de fichero");
		loadButton.setIcon(new ImageIcon("resources/icons/open.png"));
		loadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				int ret = fc.showOpenDialog(ControlPanel.this);
				if (ret == JFileChooser.APPROVE_OPTION) {
					try {
						controller.loadGame(fc.getSelectedFile().getName());
					} catch (Exception exc) {
						JOptionPane.showMessageDialog(ControlPanel.this, "El fichero seleccionado no es válido", "ERROR", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		bar.add(loadButton);
		
		bar.addSeparator();
		
		JButton saveButton = new JButton();
		saveButton.setActionCommand("save");
		saveButton.setToolTipText("Guardar la partida actual en un fichero");
		saveButton.setIcon(new ImageIcon("resources/icons/save.png"));
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				int ret = fc.showOpenDialog(ControlPanel.this);
				if (ret == JFileChooser.APPROVE_OPTION) {
					try {
						controller.saveGame(fc.getSelectedFile().getName());
					} catch (Exception exc) {
						JOptionPane.showMessageDialog(ControlPanel.this, "El fichero seleccionado no es válido", "ERROR", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		bar.add(saveButton);
		this.buttonsToBlockGameNotInitiated.add(saveButton);
		
		bar.addSeparator();
		
		JButton resetButton = new JButton();
		resetButton.setActionCommand("reset");
		resetButton.setToolTipText("Resetear el juego");
		resetButton.setIcon(new ImageIcon("resources/icons/reset.png"));
		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					controller.reset();
				}
				catch (FileNotFoundException fnfe) {
					JOptionPane.showMessageDialog(ControlPanel.this, "El fichero de reseteo no es válido", "ERROR", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		bar.add(resetButton);
		this.buttonsToBlockGameNotInitiated.add(resetButton);
		
		bar.addSeparator();
		
		JButton passButton = new JButton();
		passButton.setActionCommand("pass");
		passButton.setToolTipText("Pasar de turno");
		passButton.setIcon(new ImageIcon("resources/icons/pass.png"));
		passButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.passTurn();
			}
		});
		bar.add(passButton);
		bar.addSeparator();
		this.buttonsToBlockCPUTurn.add(passButton);
		this.buttonsToBlockGameNotInitiated.add(passButton);
		
		JButton swapButton = new JButton();
		swapButton.setActionCommand("swap");
		swapButton.setToolTipText("Intercambiar una ficha por otra aleatoriamente");
		swapButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.swapTile();
			}
		});
		swapButton.setIcon(new ImageIcon("resources/icons/swap.png"));
		bar.add(swapButton);
		this.buttonsToBlockCPUTurn.add(swapButton);
		this.buttonsToBlockGameNotInitiated.add(swapButton);
		
		bar.addSeparator();
		
		JButton continueButton = new JButton();
		continueButton.setActionCommand("continue");
		continueButton.setToolTipText("Continuar el juego");
		continueButton.setIcon(new ImageIcon("resources/icons/continue.png"));
		continueButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.update();
			}
		});
		bar.add(continueButton);
		this.buttonsToBlockGameNotInitiated.add(continueButton);
		
		bar.add(Box.createGlue());
		
		JButton exitButton = new JButton();
		exitButton.setActionCommand("exit");
		exitButton.setToolTipText("Exit from the simulator");
		exitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String[] options = {"No", "Sí"};
				int n = JOptionPane.showOptionDialog(ControlPanel.this,
						"¿Estás seguro que quieres salir?", "Salir",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE, null, options, null);
				
				if(n == 1) System.exit(0);
			}
		});
		exitButton.setIcon(new ImageIcon("resources/icons/exit.png"));
		bar.add(exitButton);	
	}
	
	@Override
	public void onWordWritten(Game game, String word, int posX, int posY, String direction, int points, int extraPoints) {
		enableButtons(this.buttonsToBlockCPUTurn, false);
	}

	@Override
	public void onPassed(Game game) {
		enableButtons(this.buttonsToBlockCPUTurn, false);
	}

	@Override
	public void onSwapped(Game game) {
		enableButtons(this.buttonsToBlockCPUTurn, false);
	}

	@Override
	public void onRegister(Game game) {
		if(!game.getGameInitiated())
			enableButtons(this.buttonsToBlockGameNotInitiated, false);
	}

	@Override
	public void onReset(Game game) {
		enableButtons(this.buttonsToBlockCPUTurn, game.humanIsPlaying());
		enableButtons(this.buttonsToBlockGameNotInitiated, true);
	}

	@Override
	public void onUpdate(Game game) {
		if(game.humanIsPlaying())
			enableButtons(this.buttonsToBlockCPUTurn, true);
	}

	@Override
	public void onError(String error) {}

	@Override
	public void onEnd() {}

	@Override
	public void onFirstTurnDecided(Game game, String[] lettersObtained) {}

	@Override
	public void onPlayersNotAdded(Game game) {}
	
	private void enableButtons(List<JButton> buttons, boolean enable) {
		
		for(JButton b : buttons)
			b.setEnabled(enable);
	}
}
