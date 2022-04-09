package gameUtils;

/* APUNTES GENERALES:

	La clase StringUtils es usada para recoger constantes útiles como el salto de línea,
	no dependiente del sistema operativo. 
	
	También incluye un método estático de eliminado de tildes en palabras.
*/

public class StringUtils {
	
	public static final String LINE_SEPARATOR = System.lineSeparator();
	public static final String DOUBLE_LINE_SEPARATOR = System.lineSeparator() + System.lineSeparator();

	public static String removeAccents(String word) {
		
		word = word.toLowerCase();
		return word.replace('á', 'a').replace('é', 'e')
		    .replace('í', 'i').replace('ó', 'o')
		    .replace('ú', 'u');
	}
}
