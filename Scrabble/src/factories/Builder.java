package factories;

import org.json.JSONObject;

/* APUNTES GENERALES
	
   La clase Builder es una clase genérica en T y abstracta
   que representa la "construcción" o instanciación de dicho tipo T.
   
   El atributo _type sirve como identificador para reconocer posteriormente
   qué tipo de objeto estamos tratando (sin romper la encapsulación).
 
 */
public abstract class Builder<T> {
	
	protected String _type;

	Builder(String type) {
		
		if (type == null)
			throw new IllegalArgumentException("Invalid type: " + type);
		
		else _type = type;
	}

	// Métodos comunes para todos los builders
	
	/* Método createInstance:
	 * Devuelve una instancia del tipo genérico T
	 */
	public T createInstance(JSONObject data) {
		return createTheInstance(data);
	}

	// Métodos abstractos
	
	/* Método createTheInstance:
	 * Cada comando sobreescribe su propio método createTheInstance con su
	 * construcción del objeto concreto.
	 * El método devuelve la instancia del objeto T construido.
	 */
	protected abstract T createTheInstance(JSONObject data);
}
