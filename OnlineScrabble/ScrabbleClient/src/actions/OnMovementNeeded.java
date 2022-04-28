package actions;

import java.util.List;

import org.json.JSONObject;

import view.ScrabbleObserver;

public class OnMovementNeeded extends OnAction {
	

	OnMovementNeeded() {
		super("movement_needed");
	}

	@Override
	OnAction interpret(JSONObject jo) {
		
		if(this.type.equals(jo.getString("type")))			
			return this;
		
		return null;
	}

	@Override
	public void executeAction(List<ScrabbleObserver> observers) {

		for(ScrabbleObserver o : observers)
			o.onMovementNeeded();
	}

}
