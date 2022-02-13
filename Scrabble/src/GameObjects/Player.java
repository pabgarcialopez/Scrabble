package GameObjects;

public class Player {
	
	private String name;
	int totalPoints;
	
	public Player(String name) {
		this.name = name;
		this.totalPoints = 0;
	}

	public String getName() {
		return name;
	}

}
