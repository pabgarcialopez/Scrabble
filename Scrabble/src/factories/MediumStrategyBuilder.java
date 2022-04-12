package factories;

import org.json.JSONObject;

import simulatedObjects.MediumStrategy;
import simulatedObjects.Strategy;

public class MediumStrategyBuilder extends StrategyBuilder {
	
	public MediumStrategyBuilder() {
		super("medium_strategy");
	}

	@Override
	protected Strategy createTheStrategy(JSONObject data) {
		
		if(data.getString("strategy").equalsIgnoreCase(_type))
			return new MediumStrategy();
		
		return null;
	}

}
