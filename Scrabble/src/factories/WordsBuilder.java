package factories;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class WordsBuilder extends Builder<List<String>>{

	public WordsBuilder() {
		super("used_words");
	}

	/* Sobrescritura del método createTheInstance:
	 * 
	 * Construye y devuelve la lista que contiene todas 
	 * las palabras válidas del juego.
	 */

	@Override
	protected List<String> createTheInstance(JSONObject data) {
		
		JSONArray jsonArrayWords = data.getJSONArray("words");
		
		List<String> words = new ArrayList<String>();
		
		for(int i = 0; i < jsonArrayWords.length(); i++)
			words.add(jsonArrayWords.getString(i));
		
		return words;
	}

}
