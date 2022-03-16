package gameObjects;

public enum SpecialEffects {

	DOUBLE_LETTER, TRIPLE_LETTER, DOUBLE_WORD, TRIPLE_WORD, CENTRE;
	
	public int getLetterPointsMultiplier() {
		
		if (DOUBLE_LETTER.equals(this)) return 2;
		
		if (TRIPLE_LETTER.equals(this)) return 3;
		
		return 1;
	}
	
	public int getWordPointsMultiplier() {
		
		if (DOUBLE_WORD.equals(this)) return 2;
		
		if(TRIPLE_WORD.equals(this)) return 3;
		
		return 1;
	}
}
