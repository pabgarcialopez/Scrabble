package interpreter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import containers.Board;
import containers.BoardBuilder;
import containers.GamePlayers;
import containers.GamePlayersBuilder;
import simulatedObjects.BoxBuilder;
import simulatedObjects.PlayerBuilder;
import simulatedObjects.TileBuilder;
import strategies.EasyStrategyBuilder;
import strategies.HardStrategyBuilder;
import strategies.HumanStrategyBuilder;
import strategies.MediumStrategyBuilder;
import strategies.StrategyBuilder;
import view.ScrabbleObserver;

public class OnRegister extends OnAction {

	private final String type = "register";
	
	private Board board;
	private int numPlayers;
	private GamePlayers gamePlayers;
	private int currentTurn;
	
	private GamePlayersBuilder gamePlayersBuilder;
	private BoardBuilder boardBuilder;
	
	OnRegister() {

		TileBuilder tileBuilder = new TileBuilder();
		
		List<StrategyBuilder> strategyBuilders = new ArrayList<StrategyBuilder>();
		
		strategyBuilders.add(new EasyStrategyBuilder());
		strategyBuilders.add(new MediumStrategyBuilder());
		strategyBuilders.add(new HardStrategyBuilder());
		strategyBuilders.add(new HumanStrategyBuilder());
		
		this.gamePlayersBuilder = new GamePlayersBuilder(new PlayerBuilder(tileBuilder, strategyBuilders));
		
		this.boardBuilder = new BoardBuilder(new BoxBuilder(tileBuilder));
	}
	
	@Override
	OnAction interpret(JSONObject jo) {

		if(this.type.equals(jo.getString("type"))) {
			
			JSONObject data = jo.getJSONObject("data");
			
			this.board = this.boardBuilder.createBoard(data.getJSONObject("game_board"));
			this.numPlayers = data.getInt("num_players");
			this.gamePlayers = this.gamePlayersBuilder.createGamePlayers(data.getJSONObject("game_players"));
			this.currentTurn = data.getInt("current_turn");
			
			return this;
		}
		
		return null;
	}

	@Override
	public void executeAction(List<ScrabbleObserver> observers) {
		for(ScrabbleObserver o : observers)
			o.onRegister(board, numPlayers, gamePlayers, currentTurn);
	}
	

}
