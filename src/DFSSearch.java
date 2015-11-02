import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class DFSSearch implements SearchAlgorithm {

	private AgentHelper agentHelper;

	public DFSSearch(AgentHelper agentHelper) {
		this.agentHelper = agentHelper;
	}

	public List<MapPoint> findPath(MapPoint startPoint, MapPoint destination) {

		List<MapPoint> path = new ArrayList<MapPoint>();

		// Make sure the map is fully explored
		for (int i = 0; i < this.agentHelper.getMap().getInternalMap().length; i++) {
			for (int j = 0; j < this.agentHelper.getMap().getInternalMap()[0].length; j++) {
				if (this.agentHelper.getMap().getInternalMap()[i][j] == null)
					return null;
			}
		}
		path = backwardSearch();
		if (path.size() > 0) {
			// Add path to blank space to start point
			// path.add(new bfs)
			for (MapPoint p : path) {
				p.setContent('o');
			}
			print();
			return path;
		} else {
			return null;
		}
	}

	public List<MapPoint> backwardSearch() {
		agentHelper.initializeSearch();
		List<MapPoint> path = new ArrayList<MapPoint>();
		Stack<MapPoint> stack = new Stack<MapPoint>();
		List<MapPoint> visitedList = new ArrayList<MapPoint>();
		List<MapPoint> changedList = new ArrayList<MapPoint>();
		
		MapPoint destination = this.agentHelper.getMap().getGoldPosition();
		destination.setDynamiteHeld(1);
		
		stack.push(destination);
		while (!stack.isEmpty()) {
			MapPoint current = stack.pop();
			visitedList.add(current);
			List<MapPoint> dynamitePosition = current.getNeighboringDynamites();
			
			for (Iterator<MapPoint> iterator = current.getNeighbors().iterator(); iterator.hasNext();) {
				MapPoint nextPoint = iterator.next();
				int dynamiteUsed = current.getDynamiteUsed();
				int dynamiteHeld = current.getDynamiteHeld();
				System.out.println("current: " + current);
				System.out.println("next   : " + nextPoint);
				
				if (nextPoint.getManhattanDistance(dynamitePosition.get(0)) <= current
						.getManhattanDistance(dynamitePosition.get(0))) {
					// If the nextPoint is wall or dynamite, update dynamiteUsed & dynamiteHeld
					if(!visitedList.contains(nextPoint)){
						if (nextPoint.getContent() == '*') {
							changedList.add(nextPoint);
							nextPoint.setContent(' ');
							nextPoint.setDynamiteUsed(++dynamiteUsed);
							nextPoint.setDynamiteHeld(--dynamiteHeld);
							print();
						} else if (nextPoint.isDynamite()) {
							nextPoint.setDynamiteHeld(++dynamiteHeld);
							nextPoint.setDynamiteUsed(dynamiteUsed);
							nextPoint.setContent('#');
							dynamitePosition.remove(nextPoint);
							print();
						} else {
							nextPoint.setDynamiteHeld(dynamiteHeld);
							nextPoint.setDynamiteUsed(dynamiteUsed);
						}
					}
					// If the nextPoint is a valid point
					if (this.agentHelper.canMoveToPoint(current, nextPoint)) {
						if (!changedList.contains(nextPoint)
								&& nextPoint.getContent() == ' ' && dynamiteHeld >= 0 ) { // && nextPoint.getRow() == 10 && nextPoint.getColumn() == 15
							reconstructPath(path, current);
							if (path.size() > 0)
								updateContent(changedList, '*');
							return path;
						}

						// if the dynamiteUsed is more than the dynamites total number in the map,
						// this means current path is not valid
						if (dynamiteUsed < 3 && !visitedList.contains(nextPoint)) {
							// If nextPoint hasn't been visited, add it to stack
							List<MapPoint> list = new ArrayList<MapPoint>();
							list.addAll(visitedList);
							list.add(current);
							list.add(nextPoint);
//							for (MapPoint p : nextPoint.getNeighbors()) {
//								if(!list.contains(p)){
//									list.add(p);
//									for (MapPoint m : p.getNeighbors()) {
//										if(!list.contains(m)){
//											list.add(m);
//											if (m.getContent() == 'd' && dynamiteHeld > -2) {
												nextPoint.setParent(current);
												visitedList.add(nextPoint);
												stack.push(nextPoint);
//											} else {
//
//											}
//										}
//									}
//								}
//							}
						}
					}
				}
			}
		}

		updateContent(changedList, '*');
		return null;
	}

	private void updateContent(List<MapPoint> changedList, char content) {
		for (MapPoint point : changedList)
			point.setContent(content);
	}

	private void reconstructPath(List<MapPoint> path, MapPoint point) {
		path.add(point); // Add to the end.
		if (point.getParent() != null) {
			reconstructPath(path, point.getParent());
		} else {
			path.remove(point);
		}
	}

	private void print() {
		MapPoint[][] mapInternal = this.agentHelper.getMap().getInternalMap();
		System.out.println(String.format("Current point: (%1$s, %2$s)",
				this.agentHelper.getAgentPosition().getRow(), this.agentHelper
						.getAgentPosition().getColumn()));
		System.out.print("+");
		for (int i = 0; i < mapInternal[0].length; i++) {
			System.out.print("=");
		}
		System.out.println("+");

		for (int i = 0; i < mapInternal.length; i++) {
			System.out.print("|");
			for (int j = 0; j < mapInternal[0].length; j++) {
				if ((i == this.agentHelper.getAgentPosition().getRow())
						&& (j == this.agentHelper.getAgentPosition()
								.getColumn()))
					System.out
							.print(this.agentHelper.getAgentDirection() == Direction.South ? "V"
									: this.agentHelper.getAgentDirection() == Direction.North ? "^"
											: this.agentHelper
													.getAgentDirection() == Direction.East ? ">"
													: "<");
				else
					System.out.print(mapInternal[i][j] == null ? "#"
							: mapInternal[i][j].getContent() + "");
			}
			System.out.println("|");
		}

		System.out.print("+");
		for (int i = 0; i < mapInternal[0].length; i++) {
			System.out.print("=");
		}
		System.out.println("+");
	}
}
