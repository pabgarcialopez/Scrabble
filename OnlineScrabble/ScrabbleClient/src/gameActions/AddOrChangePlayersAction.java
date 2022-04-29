package gameActions;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import containers.GamePlayers;
import containers.GamePlayersBuilder;
import logic.Game;
import simulatedObjects.PlayerBuilder;
import simulatedObjects.TileBuilder;
import strategies.EasyStrategyBuilder;
import strategies.HardStrategyBuilder;
import strategies.HumanStrategyBuilder;
import strategies.MediumStrategyBuilder;
import strategies.StrategyBuilder;

public class AddOrChangePlayersAction extends GameAction {

	private GamePlayers gamePlayers;
	
	private GamePlayersBuilder gamePlayersBuilder;
	
	AddOrChangePlayersAction() {
		
		super("add_or_change_players");
		
		List<StrategyBuilder> strategyBuilders = new ArrayList<StrategyBuilder>();
		
		strategyBuilders.add(new EasyStrategyBuilder());
		strategyBuilders.add(new MediumStrategyBuilder());
		strategyBuilders.add(new HardStrategyBuilder());
		strategyBuilders.add(new HumanStrategyBuilder());
		
		this.gamePlayersBuilder = new GamePlayersBuilder(new PlayerBuilder(new TileBuilder(), strategyBuilders));
	}

	@Override
	GameAction interpret(JSONObject jo) {
		
		if(this.type.equals(jo.getString("type"))) {
			
			JSONObject data = jo.getJSONObject("data");
			
			this.gamePlayers = this.gamePlayersBuilder.createGamePlayers(data.getJSONObject("game_players"));
			
			return this;
		}
		
		return null;
	}

	@Override
	public void executeAction(Game game) throws FileNotFoundException {
		game.addOrChangePlayers(gamePlayers);
	}

}
