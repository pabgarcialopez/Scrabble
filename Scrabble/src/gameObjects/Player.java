package gameObjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import gameLogic.Game;
import gameUtils.Pair;
import gameUtils.StringUtils;

/* APUNTES GENERALES:
   
   La clase Player es una clase abstracta que encapsula el modelo de un jugador.
   
   Sus atributos son:
   
   - Nombre: así será identificado el jugador.
   - Lista de fichas: son las fichas con las que el jugador puede formar palabras.
   - Puntos totales: acumulación de puntos obtenidos de colocar palabras.
   - Objeto de la clase Random: se emplea para establecer una posición de palabra
     aleatoria en los jugadores automáticos.
     
   El atributo final y constante "movingBoxes" es empleado por los jugadores automáticos
   para recorrer las posiciones del tablero de forma ordenada.
   
 */
public abstract class Player {
	
	protected static final List<Pair<Integer, Integer>> movingBoxes = 
			Collections.unmodifiableList(
					new ArrayList<Pair<Integer, Integer>>() {
						private static final long serialVersionUID = 1L;

						{
							add(new Pair<Integer, Integer>(0, -1));
							add(new Pair<Integer, Integer>(-1, 0));
							add(new Pair<Integer, Integer>(0, 1));
							add(new Pair<Integer, Integer>(1, 0));
						}
					}
			);
	
	protected String name;
	protected List<Tile> tiles;
	private int totalPoints;
	protected Random rdm;
	
	public Player(String name, int totalPoints, List<Tile> tiles) {
		this.name = name;
		this.tiles = tiles;
		this.totalPoints = totalPoints;
		this.rdm = new Random();
	}

	/* Método addTile:
	 * Añade al array de tipo Tile la ficha recibida por parámetro.
	 */
	public void addTile(Tile tile) {
		this.tiles.add(tile);
	}
	
	/* Método removeTile:
	 * Elimina del array de tipo Tile la ficha situada en el índice recibido por parámetro.
	 */
	public void removeTile(int tile) {
		this.tiles.remove(tile);		
	}
	
	/* Método removeTile:
	 * Elimina del array de tipo Tile la ficha recibida por parámetro.
	 */
	public void removeTile(Tile tile) {
		this.tiles.remove(tile);
	}
	
	/* Método getStatus:
	 * Devuelve un String con la información del jugador:
	 * - Nombre.
	 * - Puntos totales.
	 * - Fichas y sus puntos asociados.
	 */
	public String getStatus() {
		StringBuilder buffer = new StringBuilder();
		
		buffer.append("Turno de ").append(this.name).append(":")
			  .append(StringUtils.LINE_SEPARATOR);
		
		buffer.append("Puntos totales: ").append(totalPoints).append(StringUtils.LINE_SEPARATOR);
	
		buffer.append("Fichas (letra y puntos asociados a ella):").append(StringUtils.LINE_SEPARATOR);
		for(int i = 0; i < tiles.size(); i++) {
			buffer.append(tiles.get(i));
			if(i != tiles.size() - 1)
				buffer.append(" || ");
		}
		
		buffer.append(StringUtils.LINE_SEPARATOR);
		return buffer.toString();
	}

	/* Método hasLetter:
	 * Devuelve un booleano indicando si un jugador tiene en sus fichas
	 * la letra recibida por parámetro.
	 */
	public boolean hasLetter(String letter) {
		
		for(int i = 0; i < this.tiles.size(); ++i)
			if(this.tiles.get(i).getLetter().equalsIgnoreCase(letter))
				return true;
		
		return false;
	}

	/* Método numberOfTilesOf:
	 * Devuelve el número de fichas con la letra recibida 
	 * por parámetro que tiene el jugador
	 */
	public int numberOfTilesOf(String letter) {
		
		int numberOfTiles = 0;
		for (int i = 0; i < this.tiles.size(); ++i)
			if (this.tiles.get(i).getLetter().equalsIgnoreCase(letter)) 
				++numberOfTiles;
		
		return numberOfTiles;
	}

	/* Método givePoints:
	 * Suma a la cantidad actual de puntos, los recibidos por parámetro.
	 */
	public void givePoints(int points) {
		this.totalPoints += points;
	}
	
	/* Método tryWritingInBoard:
	 * 
	 * Este método es usado por los jugadores automáticos para intentar 
	 * colocar una palabra en el tablero, y devuelve un booleano
	 * indicando si se ha podido escribir una palabra o no.
	 * 
	 * Primero se comprueba si el tablero es vacío o no, pues dependiendo de ello, se podrá usar
	 * una ficha más o no (extraTile).
	 * 
	 * Después, se crea un marcaje para las letras usadas y así poder controlar cuáles se han usado ya.
	 * 
	 * Si el tablero no está vacío (extraTile == 1), con el objetivo de que el jugador automático sea 
	 * lo más uniforme posible al colocar palabras, se crean una fila y una columna aleatorias (dentro del rango del tablero),
	 * desde las cuales el jugador automático empezará a intentar formar palabras (método tryWritingAWord).
	 * 
	 *	   Puesto que se desea encadenar palabras, si la posición comprobada no tiene una ficha, seguimos buscando.
	 *	  
	 *	   En caso contrario:
	 *	   - Se añade esta ficha a la lista de fichas disponibles recibida por parámetro.
	 *	   - Se intenta escribir una palabra con los parámetros indicados (la palabra empieza siendo vacía).
	 *	   - Se elimina la última ficha añadida de la lista de fichas disponibles recibida por parámetro.
	 * 
	 * En caso de que el tablero estuviese vacío (extraTile == 0), 
	 * simplemente se llamará al método tryWritingAWord, indicando que no hay palabras en el tablero.
	 */
	protected boolean tryWritingInBoard(int wordLength, List<Tile> tilesForWord, Game game) {
		
		int extraTile = (game.getWordsInBoard() ? 1 : 0);
		
		if(wordLength > tilesForWord.size() + extraTile)
			return false;
		
		boolean wordWritten = false;
		List<Boolean> marks = createMarks(tilesForWord.size() + extraTile);
		
		if(extraTile == 1) {
			
			int randomRow = (int) this.rdm.nextDouble() * game.getBoardSize();
			int randomColumn = (int) this.rdm.nextDouble() * game.getBoardSize();
			
			for(int i = 0; i < game.getBoardSize() && !wordWritten; ++i)
				for(int j = 0; j < game.getBoardSize() && !wordWritten; ++j)  {
					
					int posX = (i + randomRow) % game.getBoardSize();
					int posY = (j + randomColumn) % game.getBoardSize();
					
					if(game.getBoard().getTile(posX, posY) != null) {
						tilesForWord.add(game.getBoard().getTile(posX, posY));
						wordWritten = tryWritingAWord("", wordLength, tilesForWord, marks, posX, posY, -1, game, true);
						tilesForWord.remove(tilesForWord.size() - 1);
					}
				}
			}
		
		else wordWritten = tryWritingAWord("", wordLength, tilesForWord, marks, -1, -1, -1, game, false);
		
		return wordWritten;
	}
	
	/*
//	* Método tryWritingInNotEmptyBoard:
//	 * 
//	 * Este método es usado por los jugadores automáticos para intentar 
//	 * colocar una palabra en un tablero no vacío, y devuelve un booleano
//	 * indicando si se ha podido escribir una palabra o no.
//	 * 
//	 * Primero se comprueba si el tamaño de la palabra que se quiere escribir es estrictamente
//	 * mayor que el número de fichas que tiene más una (resultante del encadenamiento).
//	 * 
//	 * Después, se crea un marcaje para las letras usadas y así poder controlar cuáles se han usado ya.
//	 * 
//	 * Con el objetivo de que el jugador automático sea lo más uniforme posible al colocar palabras,
//	 * se crean una fila y una columna aleatorias (dentro del rango del tablero), desde las cuales el
//	 * jugador automático empezará a intentar formar palabras.
//	 * 
//	 * Puesto que se desea encadenar palabras, si la posición comprobada no tiene una ficha, seguimos buscando.
//	 * 
//	 * En caso contrario:
//	 * - Se añade esta ficha a la lista de fichas disponibles recibida por parámetro.
//	 * - Se intenta escribir una palabra con los parámetros indicados (la palabra empieza siendo vacía).
//	 * - Se elimina la última ficha añadida de la lista de fichas disponibles recibida por parámetro.
//	 *
	protected boolean tryWritingInNotEmptyBoard(int wordLength, List<Tile> tilesForWord, Game game) {
		
		if(wordLength > tilesForWord.size() + 1)
			return false;
		
		boolean wordWritten = false;
		List<Boolean> marcaje = createMarcaje(tilesForWord.size() + 1);
		
		int randomRow = (int) this.rdm.nextDouble() * game.getBoardSize();
		int randomColumn = (int) this.rdm.nextDouble() * game.getBoardSize();
		
		for(int i = 0; i < game.getBoardSize() && !wordWritten; ++i)
			for(int j = 0; j < game.getBoardSize() && !wordWritten; ++j)  {
				
				int posX = (i + randomRow) % game.getBoardSize();
				int posY = (j + randomColumn) % game.getBoardSize();
				
				if(game.getBoard().getTile(posX, posY) != null) {
					tilesForWord.add(game.getBoard().getTile(posX, posY));
					wordWritten = tryWritingAWord("", wordLength, tilesForWord, marcaje, posX, posY, -1, game, true);
					tilesForWord.remove(tilesForWord.size() - 1);
				}
			}
				
		
		return wordWritten;
	}
	*/
	
	/*
//	 * Método tryWritingInEmptyBoard:
//	 * 
//	 * Este método es usado por los jugadores automáticos para intentar 
//	 * colocar una palabra en un tablero vacío, y devuelve un booleano
//	 * indicando si se ha podido escribir una palabra o no.
//	 * 
//	 * Primero se comprueba si el tamaño de la palabra que se quiere escribir es estrictamente
//	 * mayor que el número de fichas que tiene.
//	 * 
//	 * Después, se crea un marcaje para las letras usadas y así poder controlar cuáles se han usado ya.
//	 * 
//	 * Finalmente, se intenta escribir una palabra (posX == -1 == posY para reconocer que estamos ante un tablero
//	 * vacío y así colocar la palabra en el centro del tablero), y se devuelve el resultado de ello.
//	 *
	protected boolean tryWritingInEmptyBoard(int wordLength, List<Tile> tilesForWord, Game game) {
		
		if(wordLength > tilesForWord.size())
			return false;
		
		boolean wordWritten = false;
		
		List<Boolean> marcaje = createMarcaje(tilesForWord.size());
		
		wordWritten = tryWritingAWord("", wordLength, tilesForWord, marcaje, -1, -1, -1, game, false);
		
		return wordWritten;
	}
	*/
	
	/* Método tryWritingAWord:
	 * 
	 * Se trata de un método que sigue la técnica de vuelta atrás (backtracking) para realizar
	 * combinaciones con repetición de las letras de las fichas de la lista "tiles" y de longitud "length".
	 * 
	 * Cuando existe una posible solución (se ha formado una palabra de tamaño length), se llama al método
	 * tryDirectionsForWord. Si no, se llama recursivamente al método tryWritingAWord hasta que se alcance el tamaño length.
	 * 
	 * En el momento en el que una palabra haya sido escrita, se devolverá true en todas las llamadas recursivas.
	 */
	private boolean tryWritingAWord(String word, int wordLength, List<Tile> tilesForWord, List<Boolean> marks, int posX, int posY, int posBoardTile, Game game, boolean wordsInBoard) {
		
		for(int i = 0; i < tilesForWord.size(); ++i) {
			
			// Si la letra ya se ha usado, no entra.
			if(!marks.get(i)) {
				
				marks.set(i, Boolean.TRUE);
				
				if(i == tilesForWord.size() - 1)
					posBoardTile = word.length();
				
				word += tilesForWord.get(i).getLetter();
				
				boolean wordWritten = false;
				
				// Hay posible solución
				if(word.length() == wordLength)
					wordWritten = tryDirectionsForWord(word, posX, posY, posBoardTile, game, wordsInBoard);
				
				else wordWritten = tryWritingAWord(word, wordLength, tilesForWord, marks, posX, posY, posBoardTile, game, wordsInBoard);
				
				if(wordWritten) 
					return true;
				
				word = word.substring(0, word.length() - 1);
				
				marks.set(i, Boolean.FALSE);
			}
		}
		
		return false;
	}
	
	/* Método tryDirectionsForWord:
	 * Devuelve un booleano indicando si se ha podido escribir la palabra recibida por parámetro.
	 * 
	 * Si no hay palabras en el tablero, se intenta escribir en la casilla central (50% de probabilidad para la dirección).
	 * 
	 * Si posBoardTile != -1, se recorre el array de posiciones y se intenta escribir una palabra
	 * con las características establecidas.
	 */
	private boolean tryDirectionsForWord(String word, int posX, int posY, int posBoardTile, Game game, boolean wordsInBoard) {
		
		if(wordsInBoard && posBoardTile == -1)
			return false;
		
		if(!wordsInBoard) {
			
			Pair<Integer, Integer> center = game.getBoard().getCenter();
			
			int newPosX = center.getFirst(), newPosY = center.getSecond();
			String direction = (this.rdm.nextDouble() < 0.5 ? "V" : "H");
			
			if(game.writeAWord(word, newPosX, newPosY, direction))
				return true;
		}
		
		else if(posBoardTile != -1){
			
			for(Pair<Integer, Integer> move : movingBoxes) {
				
				int newPosX = posX, newPosY = posY;
				
				String direction;
				
				if(move.getFirst().equals(0)) {
					direction = "H";
					newPosY -= posBoardTile;
				}
				
				else {
					direction = "V";
					newPosX -= posBoardTile;
				}
				
				if(game.writeAWord(word, newPosX, newPosY, direction))
					return true;
			}
		}
		
		return false;
	}
	
	/* Método createMarks:
	 * Crea un marcaje de booleanos inicializados a false
	 * de tamaño el recibido por parámetro.
	 */
	private List<Boolean> createMarks(int size) {
		
		List<Boolean> marks = new ArrayList<Boolean>();
		for(int i = 0; i < size; ++i)
			marks.add(Boolean.FALSE);
		
		return marks;
	}
	
	// Getters
	
	public String getName() {
		return name;
	}
	
	public int getNumTiles() {
		return tiles.size();
	}
	
	public Tile getTile(int tile) {
		return this.tiles.get(tile);
	}
	
	public Tile getTile(String letter) {
		
		for(int i = 0; i < this.tiles.size(); ++i)
			if(this.tiles.get(i).getLetter().equalsIgnoreCase(letter))
				return this.tiles.get(i);
		
		return null;
	}

	public int getPoints() {
		return this.totalPoints;
	}
	
	public JSONObject report() {
		
		JSONObject jo = new JSONObject();
		
		jo.put("total_points", this.totalPoints);
		
		JSONArray tiles = new JSONArray();
		for(int i = 0; i < this.tiles.size(); ++i)
			tiles.put(this.tiles.get(i).report());
		
		jo.put("tiles", tiles);
		
		return jo;
	}

	// Métodos abstractos
	
	/* Método play:
	 * Sobrescrito (funcionalmente) por EasyPlayer, MediumPlayer y 
	 * HardPlayer para realizar la estrategia de juego correspondiente.
	 */
	public abstract void play(Game game);

	/* Método isHuman:
	 * Sobrescrito por todos los hijos de esta clase, para indicar si representan
	 * un jugador humano, o un jugador automático.
	 */
	public abstract boolean isHuman();
	
	/* Método reset:
	 * Sobrescrito (funcionalmente) por EasyPlayer, MediumPlayer y 
	 * HardPlayer para, a la hora de cargar o resetear un juego, ajustar
	 * el número de jugadores automáticos de cada tipo.
	 */
	public abstract void reset();
}
