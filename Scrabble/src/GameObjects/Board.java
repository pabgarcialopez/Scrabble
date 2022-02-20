package GameObjects;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import SpecialEffects.SpecialEffects;

public class Board {
	
	private List<List<Box>> board;
	
	public Board() {
		this.board = new ArrayList<List<Box>>();
	}
	
	public int getBoardSize() {
		return this.board.size();
	}
	
	public int getNumBoxes() {
		int numBoxes = this.getBoardSize();
		return numBoxes*numBoxes;
	}
	
	public void loadBoard(String file) {
		try(BufferedReader buffer = new BufferedReader(new FileReader(file))) {
			
			int xSize, ySize;
			String linea = buffer.readLine();
			String[] size = linea.trim().split(" ");
			xSize = Integer.parseInt(size[0]);
			ySize = Integer.parseInt(size[1]);
			for (int i = 0; i < xSize; ++i) {
				List<Box> fila = new ArrayList<Box>();
				for(int j = 0; j < ySize; ++j) {
					linea = buffer.readLine();
					linea = linea.trim();
					fila.add(new Box(i, j, SpecialEffects.stringToSpecialEffect(linea)));
				}
				this.board.add(fila);
			}					
		}
		catch (IOException ioe) {
			throw new IllegalArgumentException("Error al leer el archivo board.txt", ioe);
		}
		catch (NumberFormatException nfe) {
			throw new IllegalArgumentException("Error al leer el archivo board.txt", nfe);
		}
	}
}
