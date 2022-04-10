package logic;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import exceptions.CommandExecuteException;

/* APUNTES GENERALES
   
   La clase WordChecker es la encargada de comprobar la validez de los parámetros
   al intentar colocar una palabra en el tablero.
   
   Por ejemplo: que la palabra exista, que quepa en el tablero, ...
   
   Su único atributo es una referencia a la clase Game, para poder realizar las comprobaciones pertinentes.
 */
public final class WordChecker {
	
	private Game game;
	
	WordChecker(Game game) {
		this.game = game;
	}

	/* Método checkArguments:
	 * Contiene la estructura principal de comprobación, llamando a otros métodos de la misma clase para ello.
	 * Además, no captura la excepción que pueda lanzar alguno de los métodos.
	 */
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
		
		if (!Game.getWordsInBoard()) checkWordInCentre(word, posX, posY, direction);
		else checkWordNextToOther(word, posX, posY, direction);
		
		checkNewFormedWords(word, posX, posY, direction);
	}
	
	/* Método checkWordExists:
	 * Realiza una búsqueda binaria de la palabra recibida como parámetro en la lista de palabras válidas (ordenada), 
	 * y en caso de no encontrarse, se lanza una excepción.
	 */
	private void checkWordExists(String word) throws CommandExecuteException {
		if(Collections.binarySearch(game.getWordsList(), word.toLowerCase()) < 0)
			throw new CommandExecuteException("La palabra " + "\"" + word.toUpperCase() + "\" no existe.");
	}

	/* Método checkWordNotUsed:
	 * Realiza una búsqueda binaria de la palabra recibida como parámetro en la lista de palabras usadas (ordenada), 
	 * y en caso de no encontrarse, se lanza una excepción.
	 */
	private void checkWordNotUsed(String word) throws CommandExecuteException {
		if(Collections.binarySearch(game.getUsedWords(), word.toLowerCase()) >= 0)
			throw new CommandExecuteException("La palabra " + "\"" + word.toUpperCase() + "\" ya se encuentra en el tablero.");
	}
	
	/* Método checkDirection:
	 * Comprueba que la dirección introducida sea vertical u horizontal.
	 * En caso contrario, se lanza una excepción
	 */
	private void checkDirection(String direction) throws CommandExecuteException {
		if(!"V".equalsIgnoreCase(direction) && !"H".equalsIgnoreCase(direction))
			throw new CommandExecuteException("El argumento de la direccion no es válido.");
	}
	
	/* Método checkWordLength:
	 * Comprueba que el tamaño de la palabra no sea mayor al tamaño del tablero, y que se haya
	 * escrito una palabra de al menos dos letras. En caso contrario, se lanza una excepción.
	 */
	private void checkWordLength(String word) throws CommandExecuteException {
		if (word.length() > game.getBoardSize())
			throw new CommandExecuteException("La palabra introducida no cabe en el tablero.");
		
		if(word.length() < 2) {
			throw new CommandExecuteException("La palabra introducida debe tener al menos dos letras.");
		}
	}
	
	/* Método checkPosInRange:
	 * Comprueba que la posición de la palabra introducida se encuentre en el rango permitido.
	 * En caso contrario, se lanza una excepción.
	 */
	private void checkPosInRange(int posX, int posY) throws CommandExecuteException {
		if(posX < 0 || posX > game.getBoardSize() - 1 || posY < 0 || posY > game.getBoardSize() - 1)
			throw new CommandExecuteException("La posición introducida se sale del tablero.");
	}
	
	/* Método checkLetterInPos:
	 * Comprueba que las posiciones introducidas se ajusten al rango permitido;
	 * que en caso de solapamiento de fichas, la letra recibida por parámetro y la 
	 * letra de la ficha en el tablero coincidan; que en caso de que la casilla esté 
	 * vacá, el jugador tenga la letra correspondiente.
	 * 
	 * Además, si la casilla contiene una ficha se resta una ocurrencia al 
	 * mapa de letras necesitadas para formar una palabra.
	 */
	private void checkLetterInPos(String letter, int posX, int posY, Map<String, Integer> lettersNeeded) throws CommandExecuteException {
		
		checkPosInRange(posX, posY);
		
		if (game.getBoard().getTile(posX, posY) != null && !game.getBoard().getTile(posX, posY).getLetter().equalsIgnoreCase(letter))
			throw new CommandExecuteException(String.format("En la casilla (%s,%s) está la letra %s que no coincide con tu palabra.", posX, posY, game.getBoard().getTile(posX, posY).getLetter()));
		
		if (game.getBoard().getTile(posX, posY) == null && !game.getPlayers().playerHasLetter(game.getCurrentTurn(), letter))
			throw new CommandExecuteException("No tienes la letra \"" + letter + "\" y no se encuentra en la casilla indicada.");
		
		if (game.getBoard().getTile(posX, posY) != null)
			lettersNeeded.put(letter, lettersNeeded.get(letter) - 1);
	}
	
	/* Método checkWordInPosAndDirection:
	 * En función si la dirección es vertical u horizontal, comprueba que todas las letras de la palabra se puedan colocar en el tablero.
	 */
	private void checkWordInPosAndDirection(String word, int posX, int posY, String direction, Map<String, Integer> lettersNeeded) throws CommandExecuteException {
		
		int vertical = ("V".equalsIgnoreCase(direction) ? 1 : 0);
		int horizontal = ("H".equalsIgnoreCase(direction) ? 1 : 0);
		
		for (int i = 0; i < word.length(); ++i)
			checkLetterInPos(String.valueOf(word.charAt(i)), posX + i * vertical, posY + i * horizontal, lettersNeeded);
	}

	/* Método checkEnoughLetters:
	 * Dado el mapa de las letras necesitadas, se comprueba si el jugador tiene todas ellas.
	 * En caso contrario, se lanza una excepción.
	 */
	private void checkEnoughLetters(Map<String, Integer> lettersNeeded) throws CommandExecuteException {
		
		for (String letter : lettersNeeded.keySet()) {
			if (lettersNeeded.get(letter) > 0 
					&& game.getPlayers().numberOfTilesOf(game.getCurrentTurn(), letter) < lettersNeeded.get(letter))
				throw new CommandExecuteException("No tienes suficientes letras para colocar la palabra.");
		}
	}
	
	/* Método checkWordInCentre:
	 * Comprueba si alguna de las posiciones ocupadas por la palabra es la del centro.
	 * En caso contrario, lanza una excepción.
	 */
	private void checkWordInCentre(String word, int posX, int posY, String direction) throws CommandExecuteException {
	
		int vertical = ("V".equalsIgnoreCase(direction) ? 1 : 0);
		int horizontal = ("H".equalsIgnoreCase(direction) ? 1 : 0);
		
		for (int i = 0; i < word.length(); ++i)
			if (game.getBoard().isCenter(posX + i * vertical, posY + i * horizontal)) 
				return;
		
		throw new CommandExecuteException("La primera palabra introducida en el tablero debe situarse en la casilla central.");
	}

	/* Método checkWordNextToOther:
	 * Comprueba que alguna de las casillas ocupadas por la palabra, 
	 * sea colindante a otra palabra ya existente en el tablero.
	 * En caso contrario, lanza una excepción.
	 */
	private void checkWordNextToOther(String word, int posX, int posY, String direction) throws CommandExecuteException {
		
		int vertical = ("V".equalsIgnoreCase(direction) ? 1 : 0);
		int horizontal = ("H".equalsIgnoreCase(direction) ? 1 : 0);
		
		for (int i = 0; i < word.length(); ++i)
			if (game.getBoard().getTile(posX + i * vertical, posY + i * horizontal) != null) 
				return;
		
		
		throw new CommandExecuteException("La palabra introducida debe cortarse con alguna de las que ya están en el tablero.");
	}
	
	/* Método checkWordUnion:
	 * En función de si la dirección es vertical u horizontal, comprueba que la palabra introducida coincida
	 * con la que se forma en total en el tablero. En caso contrario, se lanza una excepción.
	 */
	private void checkWordUnion(String word, int posX, int posY, String direction) throws CommandExecuteException {
		
		if ("V".equalsIgnoreCase(direction)) {
			if((posX > 0 && game.getBoard().getTile(posX - 1, posY) != null) 
				|| ((posX + word.length() - 1) < game.getBoardSize() - 1 && game.getBoard().getTile(posX + word.length(), posY) != null))
			throw new CommandExecuteException("La palabra introducida debe ser la que se forma en total en el tablero");
		}
		
		else {
			if((posY > 0 && game.getBoard().getTile(posX, posY - 1) != null) 
					|| ((posY + word.length() - 1) < game.getBoardSize() - 1 && game.getBoard().getTile(posX, posY + word.length()) != null))
				throw new CommandExecuteException("La palabra introducida debe ser la que se forma en total en el tablero");		
		}
	}

	/* Método checkNewFormedWords:
	 * Comprueba que las palabras extras (obtenidas mediante el método getWordFormed), 
	 * que se hayan podido formar al colocar una palabra en tablero sean válidas.
	 * En caso contrario, se lanza una excepción.
	 */
	private void checkNewFormedWords(String word, int posX, int posY, String direction) throws CommandExecuteException {
		
		int vertical = ("V".equalsIgnoreCase(direction) ? 1 : 0);
		int horizontal = ("H".equalsIgnoreCase(direction) ? 1 : 0);
		
		for(int i = 0; i < word.length(); i++) {
			String newWord = getWordFormed(String.valueOf(word.charAt(i)), posX + i * vertical, posY + i * horizontal, horizontal, vertical);
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
	
	/* Método getWordFormed:
	 * Para cada letra, se recorre vertical u horizontalmente (solo una de ellas)
	 * el resto de casillas para ir formando la palabra que se forma.
	 */
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
	
	/* Método getLettersNeeded:
	 * Devuelve un mapa de clave una letra (String), y de valor el número de ocurrencias de esa letra en la palabra.
	 */
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
}
