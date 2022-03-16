package factories;

import org.json.JSONArray;
import org.json.JSONObject;

import gameContainers.Board;

public class BoardBuilder extends Builder<Board>{

	BoardBuilder() {
		super("board");
	}

	@Override
	protected Board createTheInstance(JSONObject data) {
		JSONArray jsonArrayRows  = data.getJSONArray("board");
		
		for(int i = 0; i < jsonArrayRows.length(); i++) {
			JSONArray jsonArrayBoxes = jsonArrayRows.getJSONArray(i);
			
		}
	}

}
