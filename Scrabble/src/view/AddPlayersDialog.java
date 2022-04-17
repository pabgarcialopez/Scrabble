package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.json.JSONArray;
import org.json.JSONObject;

import containers.GamePlayers;
import storage.GameLoader;
import utils.StringUtils;

public class AddPlayersDialog extends JDialog {

private static final long serialVersionUID = 1L;
	
	private static final int MAX_NUM_PLAYERS = 4;
	
	private Component parent;
	
	private DefaultComboBoxModel<Integer> numberOfPlayersModel;
	private JComboBox<Integer> numberOfPlayersCombo;
	private static final Integer[] numberOfPlayers = {2, 3, 4};
	
	private List<PlayerPanel> playerPanelList;	
	
	private JPanel playersPanel;
	
	private JButton ok;
	private JPanel buttonsPanel;
	
	private int status;
	
	AddPlayersDialog(Component parent) {
		this.parent = parent;
		this.setModal(true);
		this.playerPanelList = new ArrayList<PlayerPanel>();
		initGUI();
	}
	
	private void initGUI() {
		
		setTitle("Añadir jugadores");
		
		status = 0;
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		
		JPanel numberOfPlayersOption = new JPanel(new FlowLayout(FlowLayout.CENTER));
		mainPanel.add(numberOfPlayersOption, BorderLayout.NORTH);
		
		numberOfPlayersOption.add(new JLabel("Número de jugadores: "));
		this.numberOfPlayersModel = new DefaultComboBoxModel<Integer>();
		this.numberOfPlayersCombo = new JComboBox<Integer>(this.numberOfPlayersModel);
		numberOfPlayersOption.add(this.numberOfPlayersCombo);
		
		numberOfPlayersOption.add(Box.createRigidArea(new Dimension(5, 1)));
		
		JButton playersOkButton = new JButton();
		playersOkButton.setText("OK");
		playersOkButton.setToolTipText("Seleccionar el número de jugadores");
		playersOkButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(numberOfPlayersCombo.getSelectedItem() == null)
					JOptionPane.showMessageDialog(AddPlayersDialog.this, "Debes elegir un número de jugadores válido", "ERROR", JOptionPane.ERROR_MESSAGE);
				else {
					AddPlayersDialog.this.setVisible(false);
					
					playersPanel.removeAll();
					
					for(int i = 0; i < (int) numberOfPlayersCombo.getSelectedItem(); ++i) {
						playerPanelList.get(i).reset();
						playersPanel.add(playerPanelList.get(i));
					}
					
					buttonsPanel.add(ok);
					AddPlayersDialog.this.setLocationRelativeTo(parent);
					AddPlayersDialog.this.pack();
					AddPlayersDialog.this.setVisible(true);
				}				
			}
		});
		numberOfPlayersOption.add(playersOkButton);
		
		playersPanel = new JPanel();
		mainPanel.add(playersPanel);
		playersPanel.setLayout(new BoxLayout(playersPanel, BoxLayout.Y_AXIS));
		
		for(int i = 0; i < MAX_NUM_PLAYERS; ++i) {
			PlayerPanel p = new PlayerPanel(i + 1);
			this.playerPanelList.add(p);
		}
		
		this.buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
		
		this.ok = new JButton();
		ok.setText("OK");
		ok.setToolTipText("Añadir los jugadores seleccionados");
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!checkPlayersChosen()) {
					String help = String.format("Los valores escogidos no son válidos.%nEl nombre solo es necesario para jugadores humanos y estos nombres no se pueden repetir.");
					JOptionPane.showMessageDialog(AddPlayersDialog.this, help, "ERROR", JOptionPane.ERROR_MESSAGE);
				}
				else {
					status = 1;
					setVisible(false);
				}
			}
		});
		
		setContentPane(mainPanel);
		pack();
		setVisible(false);
	}
	
	public int open() {
		
		status = 0;
		
		buttonsPanel.removeAll();

		this.numberOfPlayersModel.removeAllElements();
		for(Integer i : numberOfPlayers)
			this.numberOfPlayersModel.addElement(i);
		
		this.numberOfPlayersCombo.setSelectedItem(null);
		
		for(PlayerPanel p : this.playerPanelList)
			p.reset();
		
		this.playersPanel.removeAll();
		
		setLocationRelativeTo(parent);
		pack();
		setVisible(true);
		
		return status;
	}
	
	public GamePlayers createPlayers() {
		
		int numPlayers = (int) this.numberOfPlayersCombo.getSelectedItem();
		
		JSONArray players = new JSONArray();
		
		while(players.length() < numPlayers) {
			JSONObject player = new JSONObject();
			
			String type = (String) this.playerPanelList.get(players.length()).getTypeSelected();
			String name = (String) this.playerPanelList.get(players.length()).getNameSelected();
			
			JSONObject strategy = new JSONObject();
			strategy.put("strategy_type",type);
			player.put("strategy", strategy);
			player.put("total_points", 0);
			
			if(!"".equals(name))
				player.put("name", name);
			
			players.put(player);
		}
		
		JSONObject data = new JSONObject();
		data.put("players", players);
	
		return GameLoader.createPlayers(data);
	}

	protected boolean checkPlayersChosen() {
		
		if(this.numberOfPlayersCombo.getSelectedItem() == null)
			return false;
		
		List<String> humanPlayerNames = new ArrayList<String>();
		
		for(int i = 0; i < (int) this.numberOfPlayersCombo.getSelectedItem(); ++i) {
			if(this.playerPanelList.get(i).getTypeSelected() == null)
				return false;
			
			if(this.playerPanelList.get(i).getTypeSelected().equals("human_strategy")) {
				if(this.playerPanelList.get(i).getNameSelected() == null || ((String)this.playerPanelList.get(i).getNameSelected()).equals(""))
					return false;
				
				humanPlayerNames.add(this.playerPanelList.get(i).getNameSelected());
			}
		}
		
		for(int i = 0; i < humanPlayerNames.size(); ++i)
			for(int j = i + 1; j < humanPlayerNames.size(); ++j)
				if(humanPlayerNames.get(i).equalsIgnoreCase(humanPlayerNames.get(j)))
					return false;
		
		return true;
	}
	
	private class PlayerPanel extends JPanel {

		private static final long serialVersionUID = 1L;
		
		private DefaultComboBoxModel<String> strategiesModel;
		private JComboBox<String> strategiesCombo;
		private final String[] strategies = { "Humana", "Fácil", "Media", "Difícil" };
		
		private JTextField nameField;
		
		PlayerPanel(int numPlayer) {
			
			setLayout(new FlowLayout(FlowLayout.CENTER));
			
			add(new JLabel("Jugador " + numPlayer + ":  "));
			
			JPanel playerStrategyPanel = new JPanel();
			playerStrategyPanel.setLayout(new BoxLayout(playerStrategyPanel, BoxLayout.X_AXIS));
			add(playerStrategyPanel);
			
			playerStrategyPanel.add(new JLabel("Estrategia: "));
			strategiesModel = new DefaultComboBoxModel<String>();
			strategiesCombo = new JComboBox<String>(this.strategiesModel);
			playerStrategyPanel.add(strategiesCombo);
			
			JPanel namePanel = new JPanel();
			namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.X_AXIS));
			add(namePanel);
			
			namePanel.add(new JLabel("Nombre: "));
			nameField = new JTextField();
			nameField.setToolTipText("Solo necesario para jugadores humanos. No se pueden repetir nombres.");
			nameField.setPreferredSize(new Dimension(60, 30));
			namePanel.add(nameField);
		}
		
		public void reset() {
			
			this.strategiesModel.removeAllElements();
			for(String type : strategies)
				this.strategiesModel.addElement(type);
			
			this.strategiesCombo.setSelectedItem(null);
			
			this.nameField.setText(null);
		}
		
		public String getTypeSelected() {
			if(this.strategiesCombo.getSelectedItem() == null)
				return null;
			
			String typeSelected = (String) this.strategiesCombo.getSelectedItem();
			
			typeSelected = StringUtils.removeAccents(typeSelected);
			typeSelected = typeSelected.toLowerCase();
			
			if("facil".equalsIgnoreCase(typeSelected))
				return "easy_strategy";
			
			else if("media".equalsIgnoreCase(typeSelected))
				return "medium_strategy";
			
			else if("dificil".equalsIgnoreCase(typeSelected))
				return "hard_strategy";
			
			else if("humano".equalsIgnoreCase(typeSelected))
				return "human_strategy";
			
			else
				return null;
		}
		
		public String getNameSelected() {
			return this.nameField.getText();
		}
	}
}
