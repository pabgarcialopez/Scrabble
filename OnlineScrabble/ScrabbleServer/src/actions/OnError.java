package actions;

import java.util.List;

import org.json.JSONObject;

import view.ScrabbleObserver;

public class OnError extends OnAction {

	private String message;
	
	OnError() {
		super("error");
	}

	@Override
	OnAction interpret(JSONObject jo) {

		if(this.type.equals(jo.getString("type"))) {
			
			JSONObject data = jo.getJSONObject("data");
			
			this.message = jo.getString(data.getString("message"));
			
			return this;
		}
		
		return null;
	}

	@Override
	public void executeAction(List<ScrabbleObserver> observers) {

		for(ScrabbleObserver o : observers)
			o.onError(this.message);
	}
	
	
}
