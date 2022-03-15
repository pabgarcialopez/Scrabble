package storage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GameLoader {

	private static final int NUMBER_OF_TILES_PER_PLAYER = 7;
	private Scanner scanner;

	public GameLoader() {
		this.scanner = new Scanner(System.in);
	}

	public void loadGame() {

		/*
		 * Formato del fichero:
		 * 
		 * 1. Numero de jugadores 
		 * 
		 * 2. Para cada jugador:
		 * 
		 * - Nombre 
		 * - Numero de puntos 
		 * - Fichas
		 * 
		 * Las tres variables anteriores, en lineas separadas.
		 * 
		 * FALTAN COSAS
		 */

		String fileName = askFileName();

		try (BufferedReader input = new BufferedReader(new FileReader(fileName))) {
			int numPlayers = input.read();
			// Aqui no se si hace falta aniadir un input.readLine();

			String[] playerNames = new String[numPlayers];
			Integer[] playerPoints = new Integer[numPlayers];
			List<List<String>> tiles = new ArrayList<List<String>>(numPlayers);

			// Para cada jugador i-esimo.
			for (int i = 0; i < numPlayers; i++) {

				// Leemos su nombre.
				playerNames[i] = input.readLine();

				// Leemos sus puntos.
				playerPoints[i] = Integer.parseInt(input.readLine());

				// Leemos sus fichas.
				for (int j = 0; j < NUMBER_OF_TILES_PER_PLAYER; j++) {

					String[] letters = input.readLine().trim().split(" ");

					for (int k = 0; k < letters.length; k++)
						tiles.get(i).add(letters[k]);
				}
			}
		}

		catch (IOException ioe) {
			System.out.println("El fichero \"" + fileName + "\" no existe.");
		}
	}

	private String askFileName() {
	
		System.out.print("Introduce el nombre del fichero en el que tienes guardada la partida: ");
		String fileName = scanner.nextLine();
		return fileName;
	}
}
       