package gameView;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

import scrabble.Controller;

public class MainWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private Controller controller;

	public MainWindow(Controller controller) {
		
		super("SCRABBLE");
		this.controller = controller;
		initGUI();
	}
	
	private void initGUI() {
		
		//this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		this.setSize(new Dimension(1000, 1000));

		JPanel mainPanel = new JPanel(new BorderLayout());
		
		mainPanel.add(new ControlPanel(this.controller, this), BorderLayout.NORTH);
		
		mainPanel.add(new BoardPanel(this.controller), BorderLayout.CENTER);
		mainPanel.add(new InfoPanel(this.controller, this), BorderLayout.SOUTH);
		
		setContentPane(mainPanel);
		this.setVisible(true);
	}
}
