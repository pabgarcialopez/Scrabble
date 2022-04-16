package view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
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
import javax.swing.SwingUtilities;

import logic.Game;
import scrabble.Controller;

public class ControlPanel extends JPanel implements ScrabbleObserver {

	private static final long serialVersionUID = 1L;
	
	private Controller controller;
	
	private JToolBar bar;
	
	private List<JButton> buttonsToBlockGameNotInitiated;
	
	private List<JButton> buttonsToBlockCPUTurn;
	
	private JButton continueButton;
	
	private JButton changeStrategyButton;
	
	private AddPlayersDialog addPlayersDialog;
	
	private JFileChooser fc;
	
	
	ControlPanel(Controller controller, Component parent) {
		
		this.controller = controller;
		
		this.buttonsToBlockCPUTurn = new ArrayList<JButton>();
		
		this.buttonsToBlockGameNotInitiated = new ArrayList<JButton>();
		
		this.addPlayersDialog = new AddPlayersDialog(parent);
		
		this.fc = new JFileChooser();
		
		this.fc.setCurrentDirectory(new File("resources/existingGames"));
		
		initGUI();
		
		this.controller.addObserver(this);
	}
	
	private void initGUI() {
		
		this.bar = new JToolBar();
		this.add(bar);
		this.bar.setPreferredSize(new Dimension(1100, 50));
		
		JButton newGameButton = new JButton();
		newGameButton.setActionCommand("newGame");
		newGameButton.setToolTipText("Iniciar una partida nueva");
		newGameButton.setIcon(new ImageIcon("resources/icons/control_panel/new_game.png"));
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
		loadButton.setIcon(new ImageIcon("resources/icons/control_panel/open.png"));
		loadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
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
		bar.add(loadButton);
		
		bar.addSeparator();
		
		JButton saveButton = new JButton();
		saveButton.setActionCommand("save");
		saveButton.setToolTipText("Guardar la partida actual en un fichero");
		saveButton.setIcon(new ImageIcon("resources/icons/control_panel/save.png"));
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				int ret = fc.showSaveDialog(ControlPanel.this);
				if (ret == JFileChooser.APPROVE_OPTION) {
					try {
						controller.saveGame(fc.getSelectedFile().getName());
						JOptionPane.showMessageDialog(ControlPanel.this, "La partida ha sido guardada con éxito", "GUARDAR", JOptionPane.INFORMATION_MESSAGE);
					} catch (FileNotFoundException | IllegalArgumentException exc) {
						JOptionPane.showMessageDialog(ControlPanel.this, exc.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
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
		resetButton.setIcon(new ImageIcon("resources/icons/control_panel/reset.png"));
		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String seedString = JOptionPane.showInputDialog(ControlPanel.this, "Introduce una semilla (actual: " + Game.getSeed() + ")", "Reset", JOptionPane.PLAIN_MESSAGE);
					
					if(seedString != null) {
						try {
							Game.setSeed(Integer.parseInt(seedString));
							controller.reset();
						}
						
						catch(NumberFormatException nfe) {
							JOptionPane.showMessageDialog(ControlPanel.this, "La semilla debe ser un número", "ERROR", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
				catch (FileNotFoundException fnfe) {
					JOptionPane.showMessageDialog(ControlPanel.this, "El fichero de reseteo no es válido", "ERROR", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		bar.add(resetButton);
		this.buttonsToBlockGameNotInitiated.add(resetButton);
		
		bar.addSeparator();
		
		JButton addPlayersButton = new JButton();
		addPlayersButton.setActionCommand("player");
		addPlayersButton.setToolTipText("Añadir o cambiar jugadores a la partida");
		addPlayersButton.setIcon(new ImageIcon("resources/icons/control_panel/player.png"));
		addPlayersButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int status = addPlayersDialog.open();
				if(status == 1) {
					controller.addPlayers(addPlayersDialog.createPlayers());
					controller.update();
				}
			}
		});
		bar.add(addPlayersButton);
		bar.addSeparator();
		this.buttonsToBlockGameNotInitiated.add(addPlayersButton);
		
		changeStrategyButton = new JButton();
		changeStrategyButton.setActionCommand("strategy");
		changeStrategyButton.setToolTipText("Cambiar las estrategias actuales de los jugadores");
		changeStrategyButton.setIcon(new ImageIcon("resources/icons/control_panel/strategy.png"));
		changeStrategyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//TODO
			}
		});
		bar.add(changeStrategyButton);
		bar.addSeparator();
		this.buttonsToBlockGameNotInitiated.add(changeStrategyButton);
		
		JButton passButton = new JButton();
		passButton.setActionCommand("pass");
		passButton.setToolTipText("Pasar de turno");
		passButton.setIcon(new ImageIcon("resources/icons/control_panel/pass.png"));
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
		swapButton.setIcon(new ImageIcon("resources/icons/control_panel/swap.png"));
		bar.add(swapButton);
		this.buttonsToBlockCPUTurn.add(swapButton);
		this.buttonsToBlockGameNotInitiated.add(swapButton);
		
		bar.addSeparator();
		
		continueButton = new JButton();
		continueButton.setActionCommand("continue");
		continueButton.setToolTipText("Continuar el juego");
		continueButton.setIcon(new ImageIcon("resources/icons/control_panel/continue.png"));
		continueButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.update();
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						controller.playTurn();
					}
				});
			}
		});
		bar.add(continueButton);
		this.buttonsToBlockGameNotInitiated.add(continueButton);
		
		bar.add(Box.createGlue());
		
		JButton helpButton = new JButton();
		helpButton.setActionCommand("help");
		helpButton.setToolTipText("Ayuda sobre cómo jugar");
		helpButton.setIcon(new ImageIcon("resources/icons/control_panel/info.png"));
		helpButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String help = String.format("• Colocar palabra: hacer click en la casilla "
						+ "donde quieres que empiece y escribir la palabra y la dirección.%n"
						+ "• Pasar de turno: pulsar el botón situado en quinta posición desde la izquierda.%n"
						+ "• Para intercambiar una ficha se debe pulsar el botón situado en sexta posición desde la izquierda.");
				JOptionPane.showMessageDialog(ControlPanel.this, help, "AYUDA", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		bar.add(helpButton);
		bar.addSeparator();
		
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
		exitButton.setIcon(new ImageIcon("resources/icons/control_panel/exit.png"));
		bar.add(exitButton);	
	}
	
	@Override
	public void onWordWritten(Game game, String word, int posX, int posY, String direction, int points, int extraPoints) {
		resetEnabledButtons(game);
	}

	@Override
	public void onPassed(Game game) {
		resetEnabledButtons(game);
	}

	@Override
	public void onSwapped(Game game) {
		resetEnabledButtons(game);
	}

	@Override
	public void onRegister(Game game) {
		resetEnabledButtons(game);
	} 

	@Override
	public void onReset(Game game) {
		resetEnabledButtons(game);
	}

	@Override
	public void onUpdate(Game game) {
		resetEnabledButtons(game);
	}

	@Override
	public void onError(String error) {}

	@Override
	public void onEnd(String message) {}

	@Override
	public void onFirstTurnDecided(Game game, String[] lettersObtained) {
		resetEnabledButtons(game);
	}
	
	@Override
	public void onMovementNeeded() {
		enableButtons(this.buttonsToBlockCPUTurn, true);
		this.continueButton.setEnabled(false);
	}
	
	private void enableButtons(List<JButton> buttons, boolean enable) {
		for(JButton b : buttons)
			b.setEnabled(enable);
	}
	
	private void resetEnabledButtons(Game game) {
		
		enableButtons(this.buttonsToBlockGameNotInitiated, Game.getGameInitiated());
		
		enableButtons(this.buttonsToBlockCPUTurn, false);
		
		this.continueButton.setEnabled(Game.getGameInitiated() && game.getNumPlayers() != 0);
		
		this.changeStrategyButton.setEnabled(Game.getGameInitiated() && game.getNumPlayers() != 0);
	}
}
