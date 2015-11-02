import java.util.LinkedList;
import java.util.Queue;

public class AgentRoutine {
	private Queue<Character> agentActions;
	private int aRow, aCol, dirn;

	public Queue<Character> getAgentActions() {
		return agentActions;
	}

	public void setAgentActions(LinkedList<Character> agentActions) {
		this.agentActions = agentActions;
	}

	public int getaRow() {
		return aRow;
	}

	public void setaRow(int aRow) {
		this.aRow = aRow;
	}

	public int getaCol() {
		return aCol;
	}

	public void setaCol(int aCol) {
		this.aCol = aCol;
	}

	public int getDirn() {
		return dirn;
	}

	public void setDirn(int dirn) {
		this.dirn = dirn;
	}

	/**
	 * @param agentActions
	 * @param aRow
	 * @param aCol
	 * @param dirn
	 */
	public AgentRoutine(Queue<Character> agentActions, int aRow, int aCol,
			int dirn) {
		super();
		this.agentActions = agentActions;
		this.aRow = aRow;
		this.aCol = aCol;
		this.dirn = dirn;
	}

	public AgentRoutine() {
		this.aRow = 0;
		this.aCol = 0;
		this.dirn = 1; // direction is North by default
		this.agentActions = new LinkedList<Character>();
	}
}
