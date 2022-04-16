package wordCheckers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exceptions.CommandExecuteException;
import logic.Game;

public class WordChecker {

	private List<Checker> checkers;
	
	private Game game;
	
	WordChecker(Game game) {
		this.game = game;
		checkers = new ArrayList<Checker>();
		
		CheckerWordExists checkerWordExists = new CheckerWordExists();
		checkers.add(checkerWordExists);
		
		CheckerWordNotUsed checkerWordNotUsed = new CheckerWordNotUsed();
		checkers.add(checkerWordNotUsed);
		
		checkers.add(new CheckerDirection());
		
		checkers.add(new CheckerNewFormedWords(checkerWordExists, checkerWordNotUsed));
	}
	
	public List<String> checkArguments(String word, int posX, int posY, String direction) throws CommandExecuteException {
		
		Map<String, Integer> lettersNeeded = getLettersNeeded(word);
		
		for(Checker checker : checkers)
			checker.check(game, word, posX, posY, direction, lettersNeeded);
		
		return getNewFormedWords(word, posX, posY, direction);
	}
	
	private Map<String, Integer> getLettersNeeded(String word) {
		
		Map<String, Integer> lettersNeeded = new HashMap<String, Integer>();
		
		for (int i = 0; i < word.length(); ++i) {
			String letter = String.valueOf(word.charAt(i));
			
			if (lettersNeeded.containsKey(letter))
				lettersNeeded.put(letter, lettersNeeded.get(letter) + 1);
			
			else lettersNeeded.put(letter, 1);
		}
		
		return lettersNeeded;
	}
	
	private List<String> getNewFormedWords(String word, int posX, int posY, String direction) {
		
		int vertical = ("V".equalsIgnoreCase(direction) ? 1 : 0);
		int horizontal = ("H".equalsIgnoreCase(direction) ? 1 : 0);
		
		List<String> newFormedWords = new ArrayList<String>();
		
		for(int i = 0; i < word.length(); i++) {
			String newWord = getWordFormed(String.valueOf(word.charAt(i)), posX + i * vertical, posY + i * horizontal, horizontal, vertical);
			if(newWord != null && newWord.length() != 1) {
				newFormedWords.add(newWord);
			}
		}
		
		return newFormedWords;
	}
	
	private String getWordFormed(String letter, int posX, int posY, int vertical, int horizontal) {
		
		if(game.getBoard().getTile(posX, posY) != null)
			return null;
		
		int auxPosX = posX - vertical;
		int auxPosY = posY - horizontal;
		
		String word = letter;
		
		while(auxPosX >= 0 && auxPosY >= 0 && game.getBoard().getTile(auxPosX, auxPosY) != null) {
			word = game.getBoard().getTile(auxPosX, auxPosY).getLetter() + word;
			auxPosX -= vertical;
			auxPosY -= horizontal;
		}
		
		auxPosX = posX + vertical;
		auxPosY = posY + horizontal;
		
		while(auxPosX < game.getBoardSize() && auxPosY < game.getBoardSize() && game.getBoard().getTile(auxPosX, auxPosY) != null) {
			word += game.getBoard().getTile(auxPosX, auxPosY).getLetter();
			auxPosX += vertical;
			auxPosY += horizontal;
		}
		
		return word;
	}
}
