package SpecialEffects;

public enum SpecialEffects {

	DOUBLE_LETTER, TRIPLE_LETTER, DOUBLE_WORD, TRIPLE_WORD;
	
	public int getLetterPointsMultiplier() {
		
		if (DOUBLE_LETTER.equals(this)) return 2;
		
		if(TRIPLE_LETTER.equals(this)) return 3;
		
		return 1;
	}
}
