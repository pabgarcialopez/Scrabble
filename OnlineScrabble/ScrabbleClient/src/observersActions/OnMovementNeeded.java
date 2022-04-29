package observersActions;

import java.util.List;

import org.json.JSONObject;

import view.ScrabbleObserver;

public class OnMovementNeeded extends OnObserverAction {
	

	OnMovementNeeded() {
		super("movement_needed");
	}

	@Override
	OnObserverAction interpret(JSONObject jo) {
		
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
