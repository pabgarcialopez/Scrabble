package client;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import org.json.JSONObject;

import control.Controller;
import observersActions.OnObserverAction;
import observersActions.OnRegister;
import server.Server;
import view.GUIView;
import view.ScrabbleObserver;

public class Client {

	
	private List<ScrabbleObserver> observers;
	
	private OnRegister register;
	
	private Server server;
	
	public Client(Server server) {
		this.observers = new ArrayList<ScrabbleObserver>();
		this.register = new OnRegister();
		this.server = server;
	}

	public void addObserver(ScrabbleObserver o) {
		if(!observers.contains(o)) {
			this.observers.add(o);
			this.register.register(this.server.getRegistration(), o);
		}
	}

	public void removeObserver(ScrabbleObserver o) {
		this.observers.remove(o);
	}
	
	public void sendGameAction(JSONObject jo) {
		this.server.doGameAction(jo);
	}

	public void doViewAction(JSONObject jo) {
		OnObserverAction observerAction = OnObserverAction.getAction(jo);
		observerAction.executeAction(observers);
	}
	
	public void initGUI() {
		
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				new GUIView(new Controller(Client.this));
			}
		});
	}
}
