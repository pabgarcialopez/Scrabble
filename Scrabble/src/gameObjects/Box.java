package gameObjects;

public class Box {

	private Tile tile;
	private SpecialEffects specialEffect;
	private boolean specialEffectAlreadyDone;
	
	public Box(SpecialEffects specialEffect) {
		this.specialEffect = specialEffect;
		this.tile = null;
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
}
