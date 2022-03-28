package gameObjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import exceptions.CommandExecuteException;
import gameLogic.Game;
import gameUtils.Pair;
import gameUtils.StringUtils;

public abstract class Player {
	
	protected String name;
	protected List<Tile> tiles;
	private int totalPoints;
	protected static final List<Pair<Integer, Integer>> movingBoxes = 
			Collections.unmodifiableList(
					new ArrayList<Pair<Integer, Integer>>() {/**
						 * 
						 */
						private static final long serialVersionUID = 1L;

					{
						add(new Pair<Integer, Integer>(0, -1));
						add(new Pair<Integer, Integer>(-1, 0));
						add(new Pair<Integer, Integer>(0, 1));
						add(new Pair<Integer, Integer>(1, 0));
					}}
			);

	// Constructor para cuando se carga partida
	public Player(String name, int totalPoints, List<Tile> tiles) {
		this.name = name;
		this.tiles = tiles;
		this.totalPoints = totalPoints;
	}
	
	// Constructor para cuando se crea nueva partida
	public Player(String name) {
		this.name = name;
		this.totalPoints = 0;
		this.tiles = new ArrayList<Tile>();
	}

	public String getName() {
		return name;
	}
	
	public void addTile(Tile tile) {
		this.tiles.add(tile);
	}
	
	public int getNumTiles() {
		return tiles.size();
	}

	public String getStatus() {
		StringBuilder buffer = new StringBuilder();
		
		// Nombre del jugador
		buffer.append("Turno de ").append(this.name).append(":")
			  .append(StringUtils.LINE_SEPARATOR);
		
		// Puntos del jugador
		buffer.append("Puntos totales: ").append(totalPoints).append(StringUtils.LINE_SEPARATOR);
	
		// Fichas del jugador
		buffer.append("Fichas (letra y puntos asociados a ella):").append(StringUtils.LINE_SEPARATOR);
		for(int i = 0; i < tiles.size(); i++) {
			buffer.append(tiles.get(i));
			if(i != tiles.size() - 1)
				buffer.append(" || ");
		}
		
		buffer.append(StringUtils.LINE_SEPARATOR);
		
		
		
		return buffer.toString();
	}

	public Tile getTile(int tile) {
		
		return this.tiles.get(tile);
	}

	public void removeTile(int tile) {

		this.tiles.remove(tile);		
	}

	public boolean hasLetter(String letter) {
		
		for(int i = 0; i < this.tiles.size(); ++i)
			if(this.tiles.get(i).getLetter().equalsIgnoreCase(letter))
				return true;
		
		return false;
	}

	public Tile getTile(String letter) {
		
		for(int i = 0; i < this.tiles.size(); ++i)
			if(this.tiles.get(i).getLetter().equalsIgnoreCase(letter))
				return this.tiles.get(i);
		
		return null;
	}

	public void removeTile(Tile tile) {

		this.tiles.remove(tile);
	}

	public int numberOfTilesOf(String letter) {
		
		int numberOfTiles = 0;
		for (int i = 0; i < this.tiles.size(); ++i)
			if (this.tiles.get(i).getLetter().equalsIgnoreCase(letter)) 
				++numberOfTiles;
		
		return numberOfTiles;
	}

	public void givePoints(int points) {
		System.out.println(String.format("¡El jugador %s gana %s puntos!%n", this.name, points));
		this.totalPoints += points;
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

	public abstract void play(Game game);
	
	public abstract boolean isHuman();
	
	protected boolean tryWritingInBoardWithWords(int wordLength, List<Tile> tilesForWord, Game game) {
		
		if(wordLength > tilesForWord.size() + 1)
			return false;
		
		boolean played = false;
		List<Boolean> marcaje = createMarcaje(tilesForWord.size() + 1);
		
		for(int i = 0; i < game.getBoardSize() && !played; ++i)
			for(int j = 0; j < game.getBoardSize() && !played; ++j) 
				if(game.getBoard().getTile(i, j) != null) {
					tilesForWord.add(game.getBoard().getTile(i, j));
					played = tryWritingAWord("", wordLength, tilesForWord, marcaje, i, j, -1, game, game.getWordsInBoard());
					tilesForWord.remove(tilesForWord.size() - 1);
				}
		
		return played;
	}
	
	protected boolean tryWritingInBoardWithoutWords(int wordLength, List<Tile> tilesForWord, Game game) {
		
		if(wordLength > tilesForWord.size())
			return false;
		
		boolean played = false;
		
		List<Boolean> marcaje = createMarcaje(tilesForWord.size());
		
		played = tryWritingAWord("", wordLength, tilesForWord, marcaje, -1, -1, -1, game, game.getWordsInBoard());
		
		return played;
	}
	
	private boolean tryWritingAWord(String word, int length, List<Tile> tiles, List<Boolean> marcaje, int posX, int posY, int posBoardTile, Game game, boolean wordsInBoard) {
		
		for(int i = 0; i < tiles.size(); ++i) {
			
			if(!marcaje.get(i)) {
				marcaje.remove(i);
				marcaje.add(i, Boolean.TRUE);
				
				if(i == tiles.size() - 1) {
					posBoardTile = word.length();
				}
				
				word += tiles.get(i).getLetter();
				
				boolean played = false;
				
				if(word.length() == length) {
					played = tryDirectionsForWord(word, posX, posY, posBoardTile, game, wordsInBoard);
				}
				else {
					played = tryWritingAWord(word, length, tiles, marcaje, posX, posY, posBoardTile, game, wordsInBoard);
				}
				
				if(played) return true;
				
				word = word.substring(0, word.length() - 1);
				
				marcaje.remove(i);
				marcaje.add(i, Boolean.FALSE);
			}
		}
		
		return false;
	}
	
	private boolean tryDirectionsForWord(String word, int posX, int posY, int posBoardTile, Game game, boolean wordsInBoard) {
		
		if(wordsInBoard && posBoardTile == -1)
			return false;
		
		if(!wordsInBoard) {
			
			int newPosX = 7, newPosY = 7;
			String direction = "V";
			
			try {
				game.checkArguments(word, newPosX, newPosY, "V");
				System.out.println(String.format("El jugador %s escribe la palabra \"%s\" en la casilla (%s, %s) con dirección \"%s\".%n", this.name, word, newPosX, newPosY, direction));
				game.writeAWord(word, newPosX, newPosY, direction);
				return true;
			}
			catch(CommandExecuteException iae) {}
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
				
				try {
					game.checkArguments(word, newPosX, newPosY, direction);
					System.out.println(String.format("El jugador %s escribe la palabra \"%s\" en la casilla (%s, %s) con dirección \"%s\".%n", this.name, word, newPosX, newPosY, direction));
					game.writeAWord(word, newPosX, newPosY, direction);
					return true;
				}
				catch(CommandExecuteException iae) {}
			}
		}
		
		return false;
	}
	
	private List<Boolean> createMarcaje(int size) {
		
		List<Boolean> marcaje = new ArrayList<Boolean>();
		for(int i = 0; i < size; ++i)
			marcaje.add(Boolean.FALSE);
		
		return marcaje;
	}
}
