package client;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import view.ScrabbleObserver;

public class Client {
	
	private Socket socket;
	
	private List<ScrabbleObserver> observers;
	
	public Client() {
		this.observers = new ArrayList<ScrabbleObserver>();
	}

	public void addObserver(ScrabbleObserver o) {
		if(!observers.contains(o))
			this.observers.add(o);
	}

	public void removeObserver(ScrabbleObserver o) {
		this.observers.remove(o);
	}
	
	public void sendData(JSONObject jo) {
		sendDataToServer(jo);
	}

	private void sendDataToServer(JSONObject jo) {
		
	}
}
