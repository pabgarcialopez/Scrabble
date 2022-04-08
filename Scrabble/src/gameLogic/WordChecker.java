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
		
		checkNewFormedWords(word, posX, posY, direction);
	}
	
	private void checkWordExists(String word) throws CommandExecuteException {
		if(Collections.binarySearch(game.getWordsList(), word.toLowerCase()) < 0)
			throw new CommandExecuteException("La palabra " + "\"" + word.toUpperCase() + "\" no existe.");
	}

	private void checkWordNotUsed(String word) throws CommandExecuteException {
		if(Collections.binarySearch(game.getUsedWords(), word.toLowerCase()) >= 0)
			throw new CommandExecuteException("La palabra " + "\"" + word.toUpperCase() + "\" ya se encuentra en el tablero.");
	}
	
	private void checkDirection(String direction) throws CommandExecuteException {
		if(!"V".equalsIgnoreCase(direction) && !"H".equalsIgnoreCase(direction))
			throw new CommandExecuteException("El argumento de la direccion no es válido.");
	}
	
	private void checkWordLength(String word) throws CommandExecuteException {
		if (word.length() > game.getBoardSize())
			throw new CommandExecuteException("La palabra introducida es demasiado larga para entrar en el tablero.");
		
		if(word.length() < 2) {
			throw new CommandExecuteException("La palabra introducida debe tener al menos dos letras.");
		}
	}
	
	private void checkPosInRange(int posX, int posY) throws CommandExecuteException {
		if(posX < 0 || posX > game.getBoardSize() - 1 || posY < 0 || posY > game.getBoardSize() - 1)
			throw new CommandExecuteException("La palabra se sale del tablero.");
	}
	
	private Map<String, Integer> getLettersNeeded(String word) {
		
		Map<String, Integer> lettersNeeded = new HashMap<String, Integer>();
		for (int i = 0; i < word.length(); ++i) {
			String letter = String.valueOf(word.charAt(i));
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
		
		int vertical = ("V".equalsIgnoreCase(direction) ? 1 : 0);
		int horizontal = ("H".equalsIgnoreCase(direction) ? 1 : 0);
		
		for (int i = 0; i < word.length(); ++i)
			checkLetterInPos(String.valueOf(word.charAt(i)), posX + i*vertical, posY + i*horizontal, lettersNeeded);
	}
	
	private void checkEnoughLetters(Map<String, Integer> lettersNeeded) throws CommandExecuteException {
		
		for (String letter : lettersNeeded.keySet()) {
			if (lettersNeeded.get(letter) > 0 
					&& game.getPlayers().numberOfTilesOf(game.getCurrentTurn(), letter) < lettersNeeded.get(letter))
				throw new CommandExecuteException("No tienes suficientes letras para colocar la palabra.");
		}
	}
	
	private void checkWordInCentre(String word, int posX, int posY, String direction) throws CommandExecuteException {
		
		int vertical = ("V".equalsIgnoreCase(direction) ? 1 : 0);
		int horizontal = ("H".equalsIgnoreCase(direction) ? 1 : 0);
		
		for (int i = 0; i < word.length(); ++i)
			if (game.getBoard().isCenter(posX + i*vertical, posY + i*horizontal)) return;
		
		
		throw new CommandExecuteException("La primera palabra introducida en el tablero debe situarse en la casilla central.");
	}
	
	private void checkWordNextToOther(String word, int posX, int posY, String direction) throws CommandExecuteException {
		
		int vertical = ("V".equalsIgnoreCase(direction) ? 1 : 0);
		int horizontal = ("H".equalsIgnoreCase(direction) ? 1 : 0);
		
		for (int i = 0; i < word.length(); ++i)
			if (game.getBoard().getTile(posX + i*vertical, posY + i*horizontal) != null) 
				return;
		
		
		throw new CommandExecuteException("La palabra introducida debe cortarse con alguna de las que ya están en el tablero.");
	}
	
	private void checkWordUnion(String word, int posX, int posY, String direction) throws CommandExecuteException {
		
		if ("V".equalsIgnoreCase(direction)) checkWordUnionVertical(word, posX, posY);
		else checkWordUnionHorizontal(word, posX, posY);
	}
	
	private void checkWordUnionVertical(String word, int posX, int posY) throws CommandExecuteException {
		
		if((posX > 0 && game.getBoard().getTile(posX - 1, posY) != null) 
				|| ((posX + word.length() - 1) < game.getBoardSize() - 1 && game.getBoard().getTile(posX + word.length(), posY) != null))
			throw new CommandExecuteException("La palabra introducida debe ser la que se forma en total en el tablero");
	}
	
	private void checkWordUnionHorizontal(String word, int posX, int posY) throws CommandExecuteException {
		
		if((posY > 0 && game.getBoard().getTile(posX, posY - 1) != null) 
				|| ((posY + word.length() - 1) < game.getBoardSize() - 1 && game.getBoard().getTile(posX, posY + word.length()) != null))
			throw new CommandExecuteException("La palabra introducida debe ser la que se forma en total en el tablero");
	
	}

	private void checkNewFormedWords(String word, int posX, int posY, String direction) throws CommandExecuteException {
		
		int vertical = ("V".equalsIgnoreCase(direction) ? 1 : 0);
		int horizontal = ("H".equalsIgnoreCase(direction) ? 1 : 0);
		
		for(int i = 0; i < word.length(); i++) {
			String newWord = getWordFormed(String.valueOf(word.charAt(i)), posX + i*vertical, posY + i*horizontal, horizontal, vertical);
			if(newWord != null && newWord.length() != 1) {
				
				try {
					checkWordExists(newWord);
					checkWordNotUsed(newWord);
					game.addUsedWord(newWord);
				}
				catch(CommandExecuteException cee) {
					throw new CommandExecuteException("Se forma la palabra adicional " + "\"" + newWord.toUpperCase() + "\".\n" + cee.getMessage(), cee);
				}
			}
		}
		
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
