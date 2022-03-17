package gameLogic;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import exceptions.CommandExecuteException;

public final class WordChecker {
	
	private Game game;
	
	WordChecker(Game game) {
		this.game = game;
	}

	public void checkArguments(String word, int posX, int posY, String direction) throws CommandExecuteException {
		
		checkWordExists(word);
		
		checkWordNotUsed(word);
		
		checkDirection(direction);
		
		checkWordLength(word);
		
		checkPosInRange(posX, posY);
		
		checkWordUnion(word, posX, posY, direction);
		
		Map<String, Integer> lettersNeeded = getLettersNeeded(word);
		
		checkWordInPosAndDirection(word, posX, posY, direction, lettersNeeded);
		
		checkEnoughLetters(lettersNeeded);
		
		if (!game.getWordsInBoard()) checkWordInCentre(word, posX, posY, direction);
		else checkWordNextToOther(word, posX, posY, direction);
		
	}
	
	private void checkWordExists(String word) throws CommandExecuteException {
		if(Collections.binarySearch(game.getWordsList(), word) < 0)
			throw new CommandExecuteException("La palabra introducida no existe.");
	}
	
	private void checkWordNotUsed(String word) throws CommandExecuteException {
		if(Collections.binarySearch(game.getUsedWords(), word) >= 0)
			throw new CommandExecuteException("La palabra introducida ya se encuentra en el tablero.");
	}
	
	private void checkDirection(String direction) throws CommandExecuteException {
		if(!"V".equalsIgnoreCase(direction) && !"H".equalsIgnoreCase(direction))
			throw new CommandExecuteException("El argumento de la direccion no es válido.");
	}
	
	private void checkWordLength(String word) throws CommandExecuteException {
		if (word.length() > game.getBoardSize())
			throw new CommandExecuteException("La palabra introducida es demasiado larga para entrar en el tablero.");
	}
	
	private void checkPosInRange(int posX, int posY) throws CommandExecuteException {
		if(posX < 0 || posX > game.getBoardSize() - 1 || posY < 0 || posY > game.getBoardSize() - 1)
			throw new CommandExecuteException("La palabra se sale del tablero.");
	}
	
	private Map<String, Integer> getLettersNeeded(String word) {
		
		Map<String, Integer> lettersNeeded = new HashMap<String, Integer>();
		for (int i = 0; i < word.length(); ++i) {
			String letter = "";
			letter += word.charAt(i);
			if (lettersNeeded.containsKey(letter)) lettersNeeded.put(letter, lettersNeeded.get(letter) + 1);
			else lettersNeeded.put(letter, 1);
		}
		
		return lettersNeeded;
	}
	
	private void checkLetterInPos(String letter, int posX, int posY, Map<String, Integer> lettersNeeded) throws CommandExecuteException {
		
		checkPosInRange(posX, posY);
		
		if (game.getBoard().getTile(posX, posY) != null && !game.getBoard().getTile(posX, posY).getLetter().equalsIgnoreCase(letter))
			throw new CommandExecuteException(String.format("En la casilla (%s,%s) está la letra %s que no coincide con tu palabra.", posX, posY, game.getBoard().getTile(posX, posY).getLetter()));
		
		if (game.getBoard().getTile(posX, posY) == null && !game.getPlayers().playerHasLetter(game.getCurrentTurn(), letter))
			throw new CommandExecuteException("No tienes la letra \"" + letter + "\" y no se encuentra en la casilla indicada.");
		
		if (game.getBoard().getTile(posX, posY) != null)
			lettersNeeded.put(letter, lettersNeeded.get(letter) - 1);
	}
	
	private void checkWordInPosAndDirection(String word, int posX, int posY, String direction, Map<String, Integer> lettersNeeded) throws CommandExecuteException {
		
		if ("V".equalsIgnoreCase(direction)) checkWordInPosVertical(word, posX, posY, lettersNeeded);
		else checkWordInPosHorizontal(word, posX, posY, lettersNeeded);
	}
	
	private void checkWordInPosVertical(String word, int posX, int posY, Map<String, Integer> lettersNeeded) throws CommandExecuteException {
		
		for (int i = 0; i < word.length(); ++i) {
			checkLetterInPos(String.valueOf(word.charAt(i)), posX + i, posY, lettersNeeded);
		}
	}
	
	private void checkWordInPosHorizontal(String word, int posX, int posY, Map<String, Integer> lettersNeeded) throws CommandExecuteException {
		
		for (int i = 0; i < word.length(); ++i) {
			checkLetterInPos(String.valueOf(word.charAt(i)), posX, posY + i, lettersNeeded);
		}
	}
	
	private void checkEnoughLetters(Map<String, Integer> lettersNeeded) throws CommandExecuteException {
		
		for (String letter : lettersNeeded.keySet()) {
			if (lettersNeeded.get(letter) > 0 
					&& game.getPlayers().numberOfTilesOf(game.getCurrentTurn(), letter) < lettersNeeded.get(letter))
				throw new CommandExecuteException("No tienes suficientes letras para colocar la palabra.");
		}
	}
	
	private void checkWordInCentre(String word, int posX, int posY, String direction) throws CommandExecuteException {
		
		if ("V".equalsIgnoreCase(direction)) checkWordInCentreVertical(word, posX, posY);
		else checkWordInCentreHorizontal(word, posX, posY);
	}
	
	private void checkWordInCentreVertical(String word, int posX, int posY) throws CommandExecuteException {

		for (int i = 0; i < word.length(); ++i) {
			if (game.getBoard().isCentre(posX + i, posY)) return;
		}
		
		throw new CommandExecuteException("La primera palabra introducida en el tablero debe situarse en la casilla central.");
	}
	
	private void checkWordInCentreHorizontal(String word, int posX, int posY) throws CommandExecuteException {

		for (int i = 0; i < word.length(); ++i) {
			if (game.getBoard().isCentre(posX, posY + i)) return;
		}
		
		throw new CommandExecuteException("La primera palabra introducida en el tablero debe situarse en la casilla central.");
	}
	
	private void checkWordNextToOther(String word, int posX, int posY, String direction) throws CommandExecuteException {
		
		if ("V".equalsIgnoreCase(direction)) checkWordNextToOtherVertical(word, posX, posY);
		else checkWordNextToOtherHorizontal(word, posX, posY);
	}
	
	private void checkWordNextToOtherVertical(String word, int posX, int posY) throws CommandExecuteException {
		
		for (int i = 0; i < word.length(); ++i) {
			if (game.getBoard().getTile(i + posX, posY) != null) return;
		}
		
		throw new CommandExecuteException("La palabra introducida debe cortarse con alguna de las que ya están en el tablero.");
	}
	
	private void checkWordNextToOtherHorizontal(String word, int posX, int posY) throws CommandExecuteException {
		
		for (int i = 0; i < word.length(); ++i) {
			if (game.getBoard().getTile(posX, i + posY) != null) return;
		}
		
		throw new CommandExecuteException("La palabra introducida debe cortarse con alguna de las que ya están en el tablero.");
	}
	
	private void checkWordUnion(String word, int posX, int posY, String direction) throws CommandExecuteException {
		
		if ("V".equalsIgnoreCase(direction)) checkWordUnionVertical(word, posX, posY);
		else checkWordUnionHorizontal(word, posX, posY);
	}
	
	private void checkWordUnionVertical(String word, int posX, int posY) throws CommandExecuteException {
		
		if((posX != 0 && game.getBoard().getTile(posX - 1, posY) != null) 
				|| ((posX + word.length() - 1) != game.getBoardSize() - 1 && game.getBoard().getTile(posX + word.length(), posY) != null))
			throw new CommandExecuteException("La palabra introducida debe ser la que se forma en total en el tablero");
	}
	
	private void checkWordUnionHorizontal(String word, int posX, int posY) throws CommandExecuteException {
		
		if((posY != 0 && game.getBoard().getTile(posX, posY - 1) != null) 
				|| ((posY + word.length() - 1) != game.getBoardSize() - 1 && game.getBoard().getTile(posX, posY + word.length()) != null))
			throw new CommandExecuteException("La palabra introducida debe ser la que se forma en total en el tablero");
	
	}
}
