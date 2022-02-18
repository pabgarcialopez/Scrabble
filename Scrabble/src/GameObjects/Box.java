package GameObjects;

import SpecialEffects.SpecialEffects;

public class Box {

	private Tile tile;
	private int x;
	private int y;
	private SpecialEffects specialEffect;
	private boolean specialEffectAlreadyDone;
	
	Box(int x, int y, SpecialEffects specialEffect) {
		this.x = x;
		this.y = y;
		this.specialEffect = specialEffect;
		this.tile = null;
		this.specialEffectAlreadyDone = false;
	}
	
	private void assignTile(Tile tile) {
		
		if (this.tile != null) 
			throw new IllegalArgumentException("The box is already occupied!");
		
		this.tile = tile;
	}
	
	private int givePoints() {
		
		int points;
		
		if(!this.specialEffectAlreadyDone) {
			this.specialEffectAlreadyDone = true;
			points = this.tile.getPoints() * this.specialEffect.getLetterPointsMultiplier();
		}
		else points = this.tile.getPoints();
		
		return points;
	}	
}
