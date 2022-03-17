package gameObjects;

import org.json.JSONObject;

public class Tile {

	private String letter;
	private int points;
	
	public Tile(String letter, int points) {
		this.letter = letter;
		this.points = points;
	}

	public int getPoints() {
	
		return this.points;
	}

	public String getLetter() {
		
		return letter;
	}
	
	@Override
	public String toString() {
		return this.letter + ": " + this.points;
	}
	
	public JSONObject report() {
		
		JSONObject jo = new JSONObject();
		jo.put("letter", this.letter);
		jo.put("points", this.points);
		
		return jo;
	}
}
