package scrabble;

import java.io.FileNotFoundException;

import gameContainers.GamePlayers;
import gameLogic.Game;
import gameView.ScrabbleObserver;
import storage.GameLoader;
import storage.GameSaver;

public class Controller {
	
	private Game game;
	
	private String lastFileUsed;
	
	Controller() {
		this.game = new Game();
		this.lastFileUsed = null;
	}
	
	public void addObserver(ScrabbleObserver o) {
		this.game.addObserver(o);
	}
	
	public void removeObserver(ScrabbleObserver o) {
		this.game.removeObserver(o);
	}
	
	public boolean writeAWord(String word, int posX, int posY, String direction) {
		return this.game.writeAWord(word, posX, posY, direction);
	}
	
	/* Método reset:
	 * Si el fichero usado más recientemente es nulo, entonces
	 * significa que no se había cargado anteriormente una partida,
	 * así que se debe resetear el juego creando una partida nueva.
	 * En caso contrario, se reseta el juego cargando la partida 
	 * asociada al fichero usado más reciente.
	 */
	public void reset() throws FileNotFoundException {
		
		if(this.lastFileUsed == null)
			newGame();
		
		else loadGame(this.lastFileUsed);
	}
	
	/* Método newGame:
	 * Delega en la clase Game el reseteo de los jugadores.
	 * Delega en la clase GameLoader la creación de una nueva partida 
	 * Establece el último fichero usado a nulo.
	 */
	public void newGame() throws FileNotFoundException {
		this.game.resetPlayers();
		GameLoader.newGame(game);
		this.lastFileUsed = null;
	}
	
	/* Método loadGame:
	 * Delega en la clase Game el reseteo de los jugadores.
	 * Delega en la clase GameLoader la carga del juego a partir 
	 * del fichero recibido por parámetro, y establece este último
	 * como el fichero usado más reciente.
	 * La excepción lanzada (fichero no encontrado), es recogida en
	 * el método execute de la clase LoadCommand.
	 */
	public void loadGame(String file) throws FileNotFoundException {
		this.game.resetPlayers();
		GameLoader.loadGame(game, file);
		this.lastFileUsed = file;
	}
	
	/* Método passTurn:
	 * Delega en la clase Game el paso de turno.
	 */
	public void passTurn() {
		this.game.passTurn();
	}
	
	/* Método swapTile:
	 * Delega en la clase Game la acción de intercambiar una ficha.
	 * Devuelve un booleano indicando si se ha podido realizar dicho intercambio.
	 */
	public boolean swapTile() {
		return this.game.swapTile();
	}
	
	/* Método update:
	 * Delega en la clase Game la actualización del estado de la partida.
	 */
	public void update() {
		this.game.update();
	}

	/* Método addPlayers:
	 * Delega en la clase Game la inicialización de los jugadores del juego.
	 */
	public void addPlayers(GamePlayers players) {
		this.game.addPlayers(players);
	}

	/* Método userExits:
	 * Delega en la clase Game la terminación del juego.
	 */
	public void userExits() {
		game.userExits();
	}

	/* Método saveGame:
	 * Delega en la clase GameSaver el guardado de partida.
	 * La excepción lanzada (fichero no encontrado), es recogida en
	 * el método execute de la clase SaveCommand.
	 */
	public void saveGame(String file) throws FileNotFoundException {
		GameSaver.saveGame(this.game, file);
	}

	public void automaticPlay() {
		//if(!game.humanIsPlaying()) 
			game.automaticPlay();
	}
}
