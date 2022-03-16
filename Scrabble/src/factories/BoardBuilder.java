package factories;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import gameContainers.Board;
import gameObjects.Box;

public class BoardBuilder extends Builder<Board>{

	private BoxBuilder boxBuilder;
	public BoardBuilder(BoxBuilder boxBuilder) {
		super("board");
		this.boxBuilder = boxBuilder;
	}

	@Override
	protected Board createTheInstance(JSONObject data) {
		
		JSONArray jsonArrayRows  = data.getJSONArray("board");
		List<List<Box>> board = new ArrayList<List<Box>>();
		
		for(int i = 0; i < jsonArrayRows.length(); i++) {
			
			JSONArray jsonArrayBoxes = jsonArrayRows.getJSONArray(i);
			List<Box> rowOfBoxes = new ArrayList<Box>();
			
			for(int j = 0; j < jsonArrayBoxes.length(); j++)
				rowOfBoxes.add(boxBuilder.createTheInstance(jsonArrayBoxes.getJSONObject(j)));
			
			board.add(rowOfBoxes);
		}
		
		return new Board(board);
	}

}
