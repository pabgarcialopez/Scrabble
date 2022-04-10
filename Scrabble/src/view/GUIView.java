package view;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import scrabble.Controller;

public class GUIView extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private Controller controller;

	public GUIView(Controller controller) {
		
		super("SCRABBLE");
		this.controller = controller;
		initGUI();
	}
	
	private void initGUI() {
		
		//this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		this.setSize(new Dimension(1130, 1000));

		JPanel mainPanel = new JPanel(new BorderLayout());
		
		mainPanel.add(new ControlPanel(this.controller, this), BorderLayout.NORTH);
		
		JPanel centerPanel = new JPanel(new BorderLayout());
		mainPanel.add(centerPanel);
		centerPanel.setPreferredSize(new Dimension(1130, 1000));
		
		JPanel boardPanel = new JPanel(new BorderLayout());
		centerPanel.add(boardPanel, BorderLayout.CENTER);
		boardPanel.setPreferredSize(new Dimension(730, 760));
		
		boardPanel.add(new BoardPanel(this.controller), BorderLayout.CENTER);
		boardPanel.add(new InfoPanel(this.controller, this), BorderLayout.NORTH);
		
		JPanel player0 = new JPanel();
		player0.add(new PlayerPanel(this.controller, 0));
		player0.setPreferredSize(new Dimension(1130, 100));
		player0.setAlignmentY(CENTER_ALIGNMENT);
		player0.setAlignmentX(CENTER_ALIGNMENT);
		
		JPanel player1 = new JPanel();
		player1.add(new PlayerPanel(this.controller, 1));
		player1.setPreferredSize(new Dimension(200, 960));
		player1.setAlignmentY(CENTER_ALIGNMENT);
		player1.setAlignmentX(CENTER_ALIGNMENT);
		
		JPanel player2 = new JPanel();
		player2.add(new PlayerPanel(this.controller, 2));
		player2.setPreferredSize(new Dimension(1130, 100));
		player2.setAlignmentY(CENTER_ALIGNMENT);
		player2.setAlignmentX(CENTER_ALIGNMENT);
		
		JPanel player3 = new JPanel();
		player3.add(new PlayerPanel(this.controller, 3));
		player3.setPreferredSize(new Dimension(200, 960));
		player3.setAlignmentY(CENTER_ALIGNMENT);
		player3.setAlignmentX(CENTER_ALIGNMENT);
		
		centerPanel.add(player0, BorderLayout.SOUTH);
		centerPanel.add(player1, BorderLayout.EAST);
		centerPanel.add(player2, BorderLayout.NORTH);
		centerPanel.add(player3, BorderLayout.WEST);
		
		setContentPane(mainPanel);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setVisible(true);
	}
}
