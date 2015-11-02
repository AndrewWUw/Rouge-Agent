
public enum Action {
	MoveForward ('F'),
	TurnLeft ('L'),
	TurnRight ('R'),
	ChopTree ('C'),
	OpenDoor ('O'),
	BlastWall ('B');
	
	private char m_value;
	private Action(char value) {
		m_value = value;
	}
	
	public char value() {
		return m_value;
	}
}