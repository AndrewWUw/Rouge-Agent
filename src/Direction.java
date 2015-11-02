
public enum Direction {
	East (0),
	North (1),
	West (2),
	South (3),
	NorthWest (4),
	NorthEast (5),
	SouthEast (6),
	SouthWest (7); 
	
	
	private int value;
	private Direction (int value) {
		this.value = value;
	}
	
	public int value() {
		return value;
	}
	
	public static Direction fromValue(int value) {
		Direction dir = North;
		if (value == 0) dir = East;
		if (value == 1) dir = North;
		if (value == 2) dir = West;
		if (value == 3) dir = South;
		if (value == 4) dir = NorthWest;
		if (value == 5) dir = NorthEast;
		if (value == 6) dir = SouthEast;
		if (value == 7) dir = SouthWest;
		return dir;
	}
}
