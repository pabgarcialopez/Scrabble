package factories;

import org.json.JSONObject;

import simulatedObjects.Strategy;

public abstract class StrategyBuilder extends Builder<Strategy> {
	
	StrategyBuilder(String type) {
		super(type);
	}

	@Override
	protected Strategy createTheInstance(JSONObject data) {
		
		return createTheStrategy(data);
			
	}
	
	protected abstract Strategy createTheStrategy(JSONObject data);

}


