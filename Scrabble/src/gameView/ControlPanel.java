package gameView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;

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
	
	ControlPanel(Controller controller) {
		
		this.controller = controller;
		
		initGUI();
		
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
				//TODO
			}
		});
		newGame.setIcon(new ImageIcon("resources/icons/new_game.png"));
		barra.add(newGame);
		barra.addSeparator();
		
		JButton load = new JButton();
		load.setActionCommand("load");
		load.setToolTipText("Load a file");
		load.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				int ret = fc.showOpenDialog(ControlPanel.this);
				if (ret == JFileChooser.APPROVE_OPTION) {
					try {
						controller.loadGame(fc.getSelectedFile().getAbsolutePath());
					} catch (Exception exc) {
						JOptionPane.showMessageDialog(ControlPanel.this, "El fichero seleccionado no es válido", "ERROR", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		load.setIcon(new ImageIcon("resources/icons/open.png"));
		barra.add(load);
		barra.addSeparator();
		
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
		barra.addSeparator();
		
		JButton exit = new JButton();
		exit.setActionCommand("exit");
		exit.setToolTipText("Exit from the simulator");
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String[] options = {"No", "Yes"};
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPassed(Game game) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSwapped(Game game) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRegister(Game game) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReset(Game game) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpdate(Game game) {}

	@Override
	public void onError(String error) {}

	@Override
	public void onEnd() {}

	@Override
	public void onFirstTurnDecided(Game game, String[] lettersObtained) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPlayersNotAdded(Game game) {
		// TODO Auto-generated method stub
		
	}
}
