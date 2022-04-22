package scrabbleTests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.Test;

import scrabble.Main;

public class MainTest {

	private static String outF;
	private static String expOutF;
	static private void run(String inFile, String outFile, String expOutFile, String mode, String seed) {

		try {
			
			outF = outFile;
			expOutF = expOutFile;
			
			String[] cadena = {"-i", inFile, "-o", outFile, "-m", mode, "-s", seed };
			Main.main(cadena);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void compareOutputs() throws JSONException, FileNotFoundException {
		
		File currRunOutFile = new File(outF);
		File expectedOutFile = new File(expOutF);
		
		JSONObject jo1 = new JSONObject(new JSONTokener(new FileInputStream(currRunOutFile)));
		JSONObject jo2 = new JSONObject(new JSONTokener(new FileInputStream(expectedOutFile)));
		
		assertTrue(jo1.similar(jo2));
		System.exit(0);
	}

	@Test
	void test_1() {
		run("resources/tests/inputs/4easyPlayersIn.txt", "resources/tests/outputs/4easyPlayersOut.json", "resources/tests/expectedOutputs/4easyPlayersExpOut.json", "console", "1389871984" );
	
	}

	/*
	@Test
	void test_2() {
		assertTrue(run("resources/tests/inputs/ex2.txt", "resources/tests/outputs/ex2.junit.out.json", "resources/tests/expectedOutputs/ex2.expout.json", "console"));
	}

	@Test
	void test_3() {
		assertTrue(run("resources/tests/inputs/ex3.txt", "resources/tests/outputs/ex3.junit.out.json", "resources/tests/expectedOutputs/ex3.expout.json", "console"));
	}
	 */
}
