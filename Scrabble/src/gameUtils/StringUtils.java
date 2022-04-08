package gameUtils;

public class StringUtils {
	
	public static final String LINE_SEPARATOR = System.lineSeparator();
	public static final String DOUBLE_LINE_SEPARATOR = System.lineSeparator() + System.lineSeparator();

	public static String removeAccents(String word) {
		
		return word.replace('á', 'a').replace('é', 'e')
		    .replace('í', 'i').replace('ó', 'o')
		    .replace('ú', 'u');
	}
}
