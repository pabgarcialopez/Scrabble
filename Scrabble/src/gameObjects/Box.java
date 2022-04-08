package gameObjects;

import org.json.JSONObject;

public class Box {

	private Tile tile;
	private SpecialEffects specialEffect;
	private boolean specialEffectAlreadyDone;
	
	// Constructor para cuando en la casilla existe una ficha
	public Box(SpecialEffects specialEffect, Tile tile) {
		this.tile = tile;
		this.specialEffect = specialEffect;
		this.specialEffectAlreadyDone = true;
	}
	
	// Constructor para cuando en la casilla NO existe una ficha
	public Box(SpecialEffects specialEffect) {
		this.tile = null;
		this.specialEffect = specialEffect;
		this.specialEffectAlreadyDone = false;
	}
	
	public void assignTile(Tile tile) {
		
		if (this.tile != null) 
			throw new IllegalArgumentException("Esta casilla ya esta ocupada!");
		
		this.tile = tile;
	}
	
	public int getPoints() {
		
		int points = this.tile.getPoints();
		
		if(!this.specialEffectAlreadyDone && this.specialEffect != null) {
			int letterMultiplier = this.specialEffect.getLetterPointsMultiplier();
			if (letterMultiplier != 1) this.specialEffectAlreadyDone = true;
			points *= letterMultiplier;
		}
		
		return points;
	}
	
	@Override
	public String toString() {
		if(tile == null)
			return " ";
		
		return tile.getLetter();
	}

	public SpecialEffects getSpecialEffect() {
		return specialEffect;
	}

	public Tile getTile() {
		
		return this.tile;
	}

	public boolean isCentre() {
		
		return SpecialEffects.CENTRE.equals(this.specialEffect);
	}

	public int getWordMultiplier() {
		
		int wordMultiplier = 1;
		
		if (!this.specialEffectAlreadyDone && this.specialEffect != null) {
			wordMultiplier = this.specialEffect.getWordPointsMultiplier();
			if (wordMultiplier != 1) this.specialEffectAlreadyDone = true;
		}
		
		return wordMultiplier;
	}
	
	public JSONObject report() {
		
		JSONObject jo = new JSONObject();
		
		if(this.specialEffect != null)
			jo.put("special_effect", this.specialEffect.toString());
		
		if(this.tile != null)
			jo.put("tile", this.tile.report());
		
		return jo;
	}
}
