package factories;

import org.json.JSONObject;

import simulatedObjects.HardStrategy;
import simulatedObjects.Strategy;

public class HardStrategyBuilder extends StrategyBuilder {
	
	public HardStrategyBuilder() {
		super("hard_strategy");
	}

	@Override
	protected Strategy createTheStrategy(JSONObject data) {
		
		if(data.getString("strategy_type").equalsIgnoreCase(_type))
			return new HardStrategy();
		
		return null;
	}

}
