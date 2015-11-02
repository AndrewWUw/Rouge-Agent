/*************************************************
 * AgentHelper.java
 * Intelligent Agent for Text-Based Adventure Game
 * COMP9414 Artificial Intelligent 
 * CSE UNSW 
 *
 * Author: Andrew WU, Walter Wang 
 * Date: 15-05-2013
 ************************************************/

import java.util.*;

public class AgentHelper {
	
	final static char Boundary = '~';
	final static ArrayList<Character> Obstacle = new ArrayList<Character>(
			Arrays.asList('T', '-', '*', '~'));
	
	private MazeMap map;
	private MapPoint agentBornPosition;			// The Home.
	private Direction agentDirection;			// Current direction in agent's mind. The born-time facing direction is North. Might be different from the Rogue map. 
	private MapPoint agentPosition;				// Current position. Always updated if the map is expanded.
	private MapPoint agentPreviousPosition;		// Previous position before the last move
	private MapPoint agentNextPosition;			// Next position to move to. Can be or not be part of a path
	private MapPoint agentDestination;			// Destination of a path. Either for getting the gold or a tool.
	
	private Action lastAction;
	private boolean lastStepMoved;
	private boolean isBackTracking = false;
	private Stack<MapPoint> visitedPointsStack = new Stack<MapPoint>();
	private Stack<MapPoint> plannedPointsStack = new Stack<MapPoint>();
	
	private boolean haveAxe = false;
	private boolean haveKey = false;
	private boolean haveGold = false;
	private int num_dynamites_held = 0;

	public AgentHelper(char[][] initialMap) {
		this.map = new MazeMap(initialMap);
		this.agentBornPosition = this.agentPosition = map.getAt(2, 2);
		this.agentDirection = Direction.North;
		planNextMove(); //Plan for the first move.
	}
	
	public MapPoint getAgentPosition() {
		return agentPosition;
	}
	
	public Direction getAgentDirection() {
		return agentDirection;
	}

	public void updateMap(char newView[][]) {
		// Remember information from last move.
		this.agentPreviousPosition = agentPosition;
		lastStepMoved = (lastAction == Action.MoveForward); 
		
		// If we moved,
		if (lastStepMoved) {
			// Update map and get agent's new position.
			this.agentPosition = map.updateMap(agentPosition, agentDirection, newView);
			debugPrintMap();
			
			// Mark the new position as visited.
			if (!this.agentPosition.isVisited())
				this.agentPosition.setVisited(true);
			
			char currentContent = agentPosition.getContent();
			// Check if we have the precious...
			if (currentContent == 'g') {
				// You beauty!, my precious...
				haveGold = true;
				agentPosition.setContent(' ');
				map.scanMapDetails();
			}
			// Check if we found a tool.
			if (currentContent == 'a') {
				haveAxe = true;
				agentPosition.setContent(' ');
				map.scanMapDetails();
			}
			if (currentContent == 'k') {
				haveKey = true;
				agentPosition.setContent(' ');
				map.scanMapDetails();
			}
			if (currentContent == 'd') {
				num_dynamites_held++;
				agentPosition.setContent(' ');
				map.scanMapDetails();
			}
			
			// Add previous position to stack for backtracking.
			if (!isBackTracking) {  //!visitedPointsStack.contains(agentPreviousPosition) && 
				visitedPointsStack.push(agentPreviousPosition);
			}
			debugPrintVisitedPoints();
			
			// Pushing neighbors into the plan stack.
			planNextMove();
		}
		else if (lastAction == Action.ChopTree || lastAction == Action.BlastWall || lastAction == Action.OpenDoor) {
			agentNextPosition.setContent(' ');
			debugPrintMap();
		}
		else 
			debugPrintMap();
	}
	
	
	public char getNextAction() {
		return getNextExploreAction();
	}
	
	public char getNextExploreAction() {
		// If we were only turning, then continue with the already planned position.
		if (!lastStepMoved && this.agentNextPosition != null) {
			debugPrintPoint("Continue with last planned point:", agentNextPosition);
		}
		else if (!plannedPointsStack.empty()) {
			// Pop next action from planned stack.
			this.agentNextPosition = plannedPointsStack.pop();
			debugPrintPoint("Popping point:", agentNextPosition);
		}
		
		// Set action.
		Action action = Action.MoveForward;
		if (this.agentNextPosition.equals(agentPosition))
			debugPrintMsg(String.format("We should move to a different point! Current == Planned (%1$s,%2$s)", 
					agentPosition.getRow(), agentPosition.getColumn()));
		
		if (this.agentNextPosition.isUpOf(agentPosition))
			action = getActionFor(Direction.North);
		else if (this.agentNextPosition.isLeftOf(agentPosition))
			action = getActionFor(Direction.West);
		else if (this.agentNextPosition.isRightOf(agentPosition))
			action = getActionFor(Direction.East);
		else
			action = getActionFor(Direction.South);
		
		// Change direction.
		if(action == Action.TurnLeft)
			if (agentDirection == Direction.South) agentDirection = Direction.East;
			else agentDirection = Direction.fromValue(agentDirection.value() + 1);
		if(action == Action.TurnRight) {
			if (agentDirection == Direction.East) agentDirection = Direction.South;
			else agentDirection = Direction.fromValue(agentDirection.value() - 1);
		}
		
		if (action == Action.MoveForward && agentNextPosition.getContent() != ' ') {
			switch (agentNextPosition.getContent()) {
			case 'T':
				action = Action.ChopTree;
				break;
			case '-':
				action = Action.OpenDoor;
				break;
			case '*':
				action = Action.BlastWall;
				num_dynamites_held--;
				break;
			}
		}
		
		// Remember the action for next step.
		this.lastAction = action;
		return action.value();
	}
	
	
	private Action getActionFor(Direction nextDirection) {
		if (agentDirection == Direction.East && nextDirection == Direction.South)
			return Action.TurnRight;
		else if (agentDirection == Direction.South && nextDirection == Direction.East)
			return Action.TurnLeft;
		else {			
			if (agentDirection.value() > nextDirection.value())
				return Action.TurnRight;
			if (agentDirection.value() < nextDirection.value())
				return Action.TurnLeft;
		}
		return Action.MoveForward;
	}
	
	private void planNextMove() {
		// Are we already on route to a destination? No need to plan anything, just continue with it.
		if (agentDestination != null && !this.agentPosition.equals(agentDestination)) {
			debugPrintPoint(String.format("Already planned for destination %1$s at", agentDestination.getContent()), agentDestination);
			return;
		}
		else
			agentDestination = null; // Clear destination.
		
		// 1. Do have have the gold? 
		if (this.haveGold && planMove(agentBornPosition)) {
			return;
		}
		
		// 2. Do we see the gold?
		if (map.getGoldPosition() != null && planMove1(map.getGoldPosition())) {
			return;
		}
		
		// 3. Do we see a tool within reach? 
		if (planForTool())
			return;

		// 4. Continue exploration.
		// Add the child nodes ie. the points in 4 directions from the current point.
		List<MapPoint> nextMoves = new ArrayList<MapPoint>();
		MapPoint nextPoint;
		
		// Push 4 neighbors except the one in the facing direction. 
		for (int i = Direction.South.value(); i >= Direction.East.value(); i--) {
			if (i == agentDirection.value())
				continue;
			
			nextPoint = agentPosition.getNeighbor(Direction.fromValue(i));
			if (nextPoint != null) {
				// First move?
				if (agentPreviousPosition == null) {
					nextMoves.add(nextPoint);
				}
				else if (!nextPoint.equals(agentPreviousPosition)) {
					nextMoves.add(nextPoint);
				}
			}
		}	
		// Lastly, push in the neighbor in the same direction.
		nextPoint = agentPosition.getNeighbor(agentDirection);
		if (nextPoint != null)
			nextMoves.add(nextPoint);
		
		// Only plan to move to a point if: we can move it to, and we haven't been there before.
		// If we have previously already planned to move there, remove those previous plans.
		debugPrintPlannedStack();
		print("Adding points to stack: ");
		int num_added = 0;
		for (MapPoint next : nextMoves) {
			if(canMoveToPoint(next) && !next.isVisited()) {
				List<MapPoint> alreadyPlanned = new ArrayList<MapPoint>();
				for (MapPoint p : plannedPointsStack) {
					if (next.equals(p))
						alreadyPlanned.add(p);
				}
				plannedPointsStack.removeAll(alreadyPlanned);
				
				// Add the point to the plan.
				plannedPointsStack.add(next);
				num_added++;
				isBackTracking = false;
				print(String.format(" %1$s (%2$s,%3$s), ", 
						next.isLeftOf(agentPosition) ? "west" : 
						next.isRightOf(agentPosition) ? "east" : 
						next.isUpOf(agentPosition) ? "north" : 
						"south", 
						next.getRow(), next.getColumn()));
			}
		}
		debugPrintMsg("");
		
		// If we didn't add any move, we are stuck,  backtrack.
		if (num_added == 0) {
			isBackTracking = true;
			if (!visitedPointsStack.isEmpty()){
				MapPoint lastPoint = visitedPointsStack.pop();
				plannedPointsStack.push(lastPoint);
				debugPrintPoint("Couldn't add any point. Poping last visited point and pushing to planned stack: ", lastPoint);
			}
			else {
				debugPrintMsg("No more points to backtrack");
			}
		}
	}
	
	private boolean planMove(MapPoint destination) {
		if (this.agentDestination == null || !this.agentDestination.equals(destination)) {
			SearchAlgorithm search = (SearchAlgorithm) new AStarSearch(this);
			List<MapPoint> pathPoints = search.findPath(agentPosition, destination);
			
			// Did we find a path?
			if (pathPoints != null) {
				agentDestination = destination;	
				print("Pushing path into stack: ");
				for (MapPoint p : pathPoints) {
					if (!p.equals(agentPosition)) {
						this.plannedPointsStack.push(p);
						debugPrintPoint(p);
					}
				}
				debugPrintPlannedStack();
				return true;
			}
			else {
				// Try naive two-way search for map7.
				if (map.getDynamitePositions().size() == 1 && num_dynamites_held == 1) {
					pathPoints = naiveTwoWaySearch(agentPosition, destination);
					if (pathPoints != null) {
						agentDestination = destination;	
						print("Pushing path into stack: ");
						for (MapPoint p : pathPoints) {
							if (!p.equals(agentPosition)) {
								this.plannedPointsStack.push(p);
								debugPrintPoint(p);
							}
						}
						debugPrintPlannedStack();
						return true;
					}
				}
			}
			return false;
		}
		else {
			// The path has already been planned, ie. the sequence of points have been pushed into stack, continue with it.
			return true;
		}
	}
	
	private boolean planMove1(MapPoint destination) {
		for(int i =0; i < this.getMap().getInternalMap().length; i++){
			for(int j = 0; j < this.getMap().getInternalMap()[0].length; j++) {
				if(this.getMap().getInternalMap()[i][j] == null)
					return false;
			}
		}
		if (this.agentDestination == null || !this.agentDestination.equals(destination)) {
			SearchAlgorithm search =  new DFSSearch(this);
			List<MapPoint> pathPoints = search.findPath(agentPosition, destination);
			
			// Did we find a path?
			if (pathPoints != null) {
				agentDestination = destination;	
				print("Pushing path into stack: ");
				for (MapPoint p : pathPoints) {
					if (!p.equals(agentPosition)) {
						this.plannedPointsStack.push(p);
						debugPrintPoint(p);
					}
				}
				debugPrintPlannedStack();
				return true;
			}
//			else {
//				// Try naive two-way search for map7.
//				if (map.getDynamitePositions().size() == 1 && num_dynamites_held == 1) {
//					pathPoints = naiveTwoWaySearch(agentPosition, destination);
//					if (pathPoints != null) {
//						agentDestination = destination;	
//						print("Pushing path into stack: ");
//						for (MapPoint p : pathPoints) {
//							if (!p.equals(agentPosition)) {
//								this.plannedPointsStack.push(p);
//								debugPrintPoint(p);
//							}
//						}
//						debugPrintPlannedStack();
//						return true;
//					}
//				}
//			}
			return false;
		}
		else {
			// The path has already been planned, ie. the sequence of points have been pushed into stack, continue with it.
			return true;
		}
	}
	// This naive method is for map 7. It assumes we currently have 2 dynamites.
	// One is already held and the other is yet to get.
	// We first get all the neighbors around the gold, then check if we can find a path
	// from the second dynamite to each neighbor.
	private List<MapPoint> naiveTwoWaySearch(MapPoint start, MapPoint goal) {
		MapPoint dynamite2 = map.getDynamitePositions().get(0);
	
		List<MapPoint> fullPath = null;
		
		List<MapPoint> neighbors = goal.getNeighbors();
		for (MapPoint neighbor : neighbors) {
			AStarSearch search = new AStarSearch(this);

			if (neighbor.isWall()) {
				// Trick: Temporarily make this wall disappear
				neighbor.setContent(' ');
				
				List<MapPoint> d2ToG = search.findPath(dynamite2, goal);
				// If d2 can find a thru pass to gold, then any wall encountered along the path can be
				// temporarily disappear. this makes the agent prefer d2's path instead of other path to the neighbor

				if (d2ToG != null) {
					d2ToG.remove(d2ToG.size() - 1);
					List<MapPoint> tempClearedPoints = new ArrayList<MapPoint>();
					for	(MapPoint p : d2ToG) {
						if (p.isWall()) {
							// Trick: Temporarily make this wall disappear
							p.setContent(' ');
							tempClearedPoints.add(p);
						}
					}
					
					this.initializeSearch();
					List<MapPoint> d1ToN = search.findPath(start, neighbor);
					this.initializeSearch();
					List<MapPoint> nToD2 = search.findPath(neighbor, dynamite2);
					if (d1ToN != null && nToD2 != null) {
						// We have the path!
						fullPath = new ArrayList<MapPoint>();
						fullPath.addAll(d2ToG);							// Add path of d2 to Gold.
						fullPath.addAll(unionPath(d1ToN, nToD2));		// Add path of d1 to neighbor and neighbor to d2
					}

					// Restore the wall.
					for	(MapPoint p : tempClearedPoints) {
						p.setContent('*');
					}
				}

				// Restore the wall.
				neighbor.setContent('*');
				if (fullPath != null)
					return fullPath;
			}
		}
		
		return null;
	}
	

	private List<MapPoint> unionPath(List<MapPoint> fromList, List<MapPoint> toList) {
		List<MapPoint> returnedList = new ArrayList<MapPoint>();
		List<MapPoint> overlappedList = new ArrayList<MapPoint>();
		MapPoint intersectionPoint = null;
		// First add toList.
		for (MapPoint p : toList) {
			if (fromList.contains(p)) {
				if (intersectionPoint == null) {
					intersectionPoint = p;
					overlappedList.add(p);
					returnedList.add(intersectionPoint);
				}
				else
					overlappedList.add(p);
			}
			else
				returnedList.add(p);
		}
		
		// Then add fromList.
		for (MapPoint p : fromList) {
			if (!overlappedList.contains(p))
				returnedList.add(p);
		}
		return returnedList;
	}
	
	private boolean planForTool() {
		SearchAlgorithm search = (SearchAlgorithm) new AStarSearch(this);
		MapPoint destination = null;
		List<MapPoint> pathPoints = null;

		// First plan for dynamite.
		if (map.getDynamitePositions().size() > 0) {
			destination = map.getDynamitePositions().get(0);
			pathPoints = search.findPath(agentPosition, destination);
		}
		
		// Then plan for axe.
		if (pathPoints == null && !this.haveAxe && map.getAxePosition() != null) {
			destination = map.getAxePosition();
			pathPoints = search.findPath(agentPosition, destination);
		}
		
		// Then plan for key.
		if (pathPoints == null && !this.haveKey && map.getKeyPosition() != null) {
			destination = map.getKeyPosition();
			pathPoints = search.findPath(agentPosition, destination);
		}

		// Did we find a path?
		if (pathPoints != null) {
			agentDestination = destination;	
			print("Pushing path into stack: ");
			for (MapPoint p : pathPoints) {
				if (!p.equals(agentPosition)) {
					this.plannedPointsStack.push(p);
					debugPrintPoint(p);
				}
			}
			debugPrintPlannedStack();
			return true;
		}
		else {
			agentDestination = null;
			return false;
		}
	}

	public void initializeSearch() {
		map.cleanupSearch();
	}
	
	public boolean canMoveToPoint(MapPoint newPoint) {
		char target = newPoint.getContent();
		if (target == ' ' || target == 'a' || target == 'k' || target == 'd' || target == 'g'
			|| (target == 'T' && haveAxe)
			|| (target == '-' && haveKey))
			return true;
		return false;
	}
	
	public boolean canMoveToPoint(MapPoint startPoint, MapPoint newPoint) {
		char target = newPoint.getContent();
		if (target == ' ' || target == 'a' || target == 'k' || target == 'd' || target == 'g'
			|| (target == 'T' && haveAxe)
			|| (target == '-' && haveKey)
			|| (target == '*' && map.getGoldPosition() != null && startPoint.getDynamiteHeld() > 0))
			return true;
		return false;
	}
	
	//===============================
	// Debugging related
	//===============================
	public void print(String text) {
		if (true) 
			System.out.print(text);
	}
	public void println() {
		print("\n");
	}
	public void println(String text) {
		print(text);
		print("\n");
	}
	private void debugPrintMsg(String text) {
		println(text);
	}
	public void debugPrintPoint(String msg, MapPoint p) {
		print(msg);
		debugPrintPoint(p);
		println();
	}
	private void debugPrintPoint(MapPoint p) {
		print(String.format(" (%1$s,%2$s)", p.getRow(), p.getColumn()));
	}
	private void debugPrintPlannedStack() {
		print(String.format("\n%1$s elements currently in the plan stack: ", plannedPointsStack.size()));
		for (MapPoint p : plannedPointsStack) {
			debugPrintPoint(p);
		}
		println();
	}
	private void debugPrintVisitedPoints() {
		print("Visited points stack, last 10: ");
		for (int i = Math.max(0, visitedPointsStack.size() - 10); i < visitedPointsStack.size(); ++i) {
			debugPrintPoint(visitedPointsStack.get(i));
		}
		println();
	}
	private void debugPrintMap() {
		MapPoint[][] mapInternal = this.map.getInternalMap();
		println(String.format("Current point: (%1$s, %2$s)", agentPosition.getRow(), agentPosition.getColumn()));
		print("+");
		for (int i = 0; i < mapInternal[0].length; i++) {
			print("=");	
		}
		println("+");
		
		for (int i = 0; i < mapInternal.length; i++) {
			print("|");
			for (int j = 0; j < mapInternal[0].length; j++) {
				if ((i == this.agentPosition.getRow()) && (j == this.agentPosition.getColumn()))
					print(agentDirection == Direction.South ? "V" : agentDirection == Direction.North ? "^" : agentDirection == Direction.East ? ">" : "<");
				else
					print(mapInternal[i][j] == null ? "#" : mapInternal[i][j].getContent() + "");	
			}
			println("|");
		}
		
		print("+");
		for (int i = 0; i < mapInternal[0].length; i++) {
			print("=");	
		}
		println("+");
	}
	
	//===============================
	// Public properties
	//===============================
	
	public int getNum_dynamites_held() {
		return num_dynamites_held;
	}

	public void setNum_dynamites_held(int num_dynamites_held) {
		this.num_dynamites_held = num_dynamites_held;
	}

	public boolean isHave_axe() {
		return haveAxe;
	}

	public void setHave_axe(boolean have_axe) {
		this.haveAxe = have_axe;
	}

	public boolean isHave_key() {
		return haveKey;
	}

	public void setHave_key(boolean have_key) {
		this.haveKey = have_key;
	}

	public boolean isHave_gold() {
		return haveGold;
	}

	public void setHave_gold(boolean have_gold) {
		this.haveGold = have_gold;
	}
	
	public int getDirn() {
		return agentDirection.value();
	}

	public MazeMap getMap() {
		return map;
	}
}