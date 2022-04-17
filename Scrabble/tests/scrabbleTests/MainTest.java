package scrabbleTests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileInputStream;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.Test;

import scrabble.Main;

class MainTest {

	static private boolean run(String inFile, String outFile, String expOutFile, String mode) {

		try {
			
			String[] cadena = {"-i", inFile, "-o", outFile, "-m", mode };
			Main.main(cadena);
			scrabble.Main.main(new String[] { "-i", inFile, "-o", outFile, "-m", mode });

			File currRunOutFile = new File(outFile);
			File expectedOutFile = new File(expOutFile);
			
			JSONObject jo1 = new JSONObject(new JSONTokener(new FileInputStream(currRunOutFile)));
			JSONObject jo2 = new JSONObject(new JSONTokener(new FileInputStream(expectedOutFile)));
			
			//currRunOutFile.delete();
			
			return jo1.similar(jo2);
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	void test_1() {
		assertTrue(run("resources/tests/inputs/ex1.json", "resources/tests/outputs/ex1.junit.out.json", "resources/tests/expectedOutputs/ex1.expout.json", "console"));

	}

	@Test
	void test_2() {
		assertTrue(run("resources/tests/inputs/ex2.json", "resources/tests/outputs/ex2.junit.out.json", "resources/tests/expectedOutputs/ex2.expout.json", "console"));
				

	}

	@Test
	void test_3() {
		assertTrue(run("resources/tests/inputs/ex3.json", "resources/tests/outputs/ex3.junit.out.json", "resources/tests/expectedOutputs/ex3.expout.json", "console"));
				

	}

}
