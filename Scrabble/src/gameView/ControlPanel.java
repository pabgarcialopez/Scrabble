package gameView;

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
	
	private JToolBar barra;
	
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
		
		barra = new JToolBar();
		this.add(barra);
		
		JButton newGame = new JButton();
		newGame.setActionCommand("newGame");
		newGame.setToolTipText("Iniciar una partida nueva");
		newGame.addActionListener(new ActionListener() {
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
		newGame.setIcon(new ImageIcon("resources/icons/new_game.png"));
		barra.add(newGame);
		barra.addSeparator();
		
		JButton load = new JButton();
		load.setActionCommand("load");
		load.setToolTipText("Cargar una partida de fichero");
		load.addActionListener(new ActionListener() {
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
		load.setIcon(new ImageIcon("resources/icons/open.png"));
		barra.add(load);
		barra.addSeparator();
		
		JButton save = new JButton();
		save.setActionCommand("save");
		save.setToolTipText("Guardar la partida actual en un fichero");
		save.addActionListener(new ActionListener() {
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
		save.setIcon(new ImageIcon("resources/icons/save.png"));
		this.buttonsToBlockGameNotInitiated.add(save);
		
		JButton reset = new JButton();
		reset.setActionCommand("reset");
		reset.setToolTipText("Resetear el juego");
		reset.addActionListener(new ActionListener() {
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
		reset.setIcon(new ImageIcon("resources/icons/reset.png"));
		barra.add(reset);
		this.buttonsToBlockGameNotInitiated.add(reset);
		
		barra.addSeparator();
		
		barra.add(Box.createGlue());
		
		JButton pass = new JButton();
		pass.setActionCommand("pass");
		pass.setToolTipText("Pasar de turno");
		pass.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.passTurn();
			}
		});
		pass.setIcon(new ImageIcon("resources/icons/pass.png"));
		barra.add(pass);
		barra.addSeparator();
		this.buttonsToBlockCPUTurn.add(pass);
		this.buttonsToBlockGameNotInitiated.add(pass);
		
		JButton swap = new JButton();
		swap.setActionCommand("swap");
		swap.setToolTipText("Intercambiar una ficha");
		swap.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.swapTile();
			}
		});
		swap.setIcon(new ImageIcon("resources/icons/swap.png"));
		barra.add(swap);
		this.buttonsToBlockCPUTurn.add(swap);
		this.buttonsToBlockGameNotInitiated.add(swap);
		
		barra.addSeparator();
		
		JButton continuar = new JButton();
		continuar.setActionCommand("continue");
		continuar.setToolTipText("Continuar el juego");
		continuar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.update();
			}
		});
		continuar.setIcon(new ImageIcon("resources/icons/continue.png"));
		barra.add(continuar);
		this.buttonsToBlockGameNotInitiated.add(continuar);
		
		barra.addSeparator();
		
		JButton exit = new JButton();
		exit.setActionCommand("exit");
		exit.setToolTipText("Exit from the simulator");
		exit.addActionListener(new ActionListener() {
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
		exit.setIcon(new ImageIcon("resources/icons/exit.png"));
		barra.add(exit);	
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
