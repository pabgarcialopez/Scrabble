package factories;

import org.json.JSONObject;

public abstract class Builder<T> {
	
	protected String _type;

	Builder(String type) {
		
		if (type == null)
			throw new IllegalArgumentException("Invalid type: " + type);
		
		else _type = type;
	}

	public T createInstance(JSONObject info) {

		T b = null;

		b = createTheInstance(info);
		
		return b;
	}

	protected abstract T createTheInstance(JSONObject data);
}
