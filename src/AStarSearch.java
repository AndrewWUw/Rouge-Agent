import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;


public class AStarSearch implements SearchAlgorithm {
	private AgentHelper agentHelper;
	
	public AStarSearch(AgentHelper agentHelper) {
		this.agentHelper = agentHelper;
	}

	@Override
	public List<MapPoint> findPath(MapPoint start, MapPoint goal) {
		PriorityQueue<MapPoint> openSet = new PriorityQueue<MapPoint>();
		List<MapPoint> closedSet = new ArrayList<MapPoint>();
		List<MapPoint> path = new ArrayList<MapPoint>();
		
		agentHelper.debugPrintPoint(String.format("Calculating a path using A* to get %1$s at", goal.getContent()), goal);
		agentHelper.initializeSearch();
		
		// Enqueue OPEN.
		openSet.offer(start);
		
		// Initial costs.
		int g = 0;
		int h = getManhattanCost(start, goal);
		int f = g + h;
		// Initial dynamite is known by agent helper.
		int dynamite = agentHelper.getNum_dynamites_held();
		start.setgCost(0);
		start.sethCost(h);
		start.setfCost(f);
		start.setDynamiteHeld(dynamite);

		while (openSet.size() > 0) {
			MapPoint current = openSet.poll();
			
			if (current.equals(goal)) {
				agentHelper.println("Found a path to goal");
				reconstructPath(path, goal);
				return path;
			}

			// Add to CLOSED.
			closedSet.add(current);
			
			for (MapPoint neighbor : current.getNeighbors()) {
				if (agentHelper.canMoveToPoint(current, neighbor)) {
					// Increment g.
					int tentative_gcost = current.getgCost() + 1;
					
					// Calculate dynamite usage.
					int dynamiteHeld = current.getDynamiteHeld();
					int dynamiteUsed = current.getDynamiteUsed();
					
					if (neighbor.isWall()) {
						dynamiteHeld--;
						dynamiteUsed++;
					}
					else if (neighbor.isDynamite())
						dynamiteHeld++;
					
					if (closedSet.contains(neighbor) && tentative_gcost >= neighbor.getgCost())
						continue;
					
					// This neighbor has not been evaluated before?
					if (!openSet.contains(neighbor)) {
						neighbor.setParent(current);						// Set current as neighbor's parent.
						neighbor.setDynamiteHeld(dynamiteHeld);				// Number of dynamites we will end up with if we move to this neighbor via the current point.
						neighbor.setDynamiteUsed(dynamiteUsed);				// Number of dynamites we would have used if we move to this neighbor via the current point.

						neighbor.setgCost(tentative_gcost); 									// Neighbor's g cost includes a step from current
						neighbor.sethCost(getManhattanCost(neighbor, goal));					// Neighbor's h cost
						neighbor.setfCost(tentative_gcost + getManhattanCost(neighbor, goal));	// Neighbor's f cost
						
						// Enqueue.
						openSet.add(neighbor);
					}
					
					// This neighbor has been evaluated before and was already part of a path. 
					// However, if the old cost associated with this neighbor is higher than the current
					// point's cost, that means it's a better path to get to this neighbor via the current point.
					// we should change the neighbor's parent to the current point.
					else if (current.costLessRegardlessHCost(neighbor)) { 	// current's cost < neighbor's cost
						neighbor.setParent(current);						// Plan to move to this neighbor via the current.
						neighbor.setDynamiteHeld(dynamiteHeld);				// Number of dynamites we will end up with if we move to this neighbor via the current point.
						neighbor.setDynamiteUsed(dynamiteUsed);				// Number of dynamites we would have used if we move to this neighbor via the current point.
						neighbor.setgCost(tentative_gcost);					// The accumulated gcost
						neighbor.setfCost(tentative_gcost + neighbor.gethCost());	// The accumulated fcost
					}
				}
			}
		}
		agentHelper.println("No Path to goal, how sad...");
		return null;
	}

	private void reconstructPath(List<MapPoint> path, MapPoint point) {
		path.add(point); // Add to the end.
		if (point.getParent() != null)
			reconstructPath(path, point.getParent());
	}

    private static int getManhattanCost(MapPoint a, MapPoint b)
    {
        return (Math.abs(a.getRow() - b.getRow()) + Math.abs(a.getColumn() - b.getColumn()));
    }
}
