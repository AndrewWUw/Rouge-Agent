/**************************************
 * BFSSearch.java 
 * Perform breadth first search on a 
 * given map
 * 
 * Author: Andrew WU
 * Date: 19-05-2013
 * 
 *************************************/
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;

public class BFSSearch implements SearchAlgorithm {
	private MazeMap mazeMap;
	private MapPoint currentPos;
	private AgentHelper agentHelper;

	public AgentHelper getAgentHelper() {
		return agentHelper;
	}

	public void setAgentHelper(AgentHelper agentHelper) {
		this.agentHelper = agentHelper;
	}

	public MazeMap getMazeMap() {
		return mazeMap;
	}

	public void setMazeMap(MazeMap mazeMap) {
		this.mazeMap = mazeMap;
	}

	public MapPoint getCurrentPos() {
		return currentPos;
	}

	public void setCurrentPos(MapPoint currentPos) {
		this.currentPos = currentPos;
	}

	public BFSSearch() {
		super();
		this.mazeMap = new MazeMap(new char[0][0]);
		this.currentPos = new MapPoint();
		this.agentHelper = new AgentHelper(new char[0][0]);
	}

	public BFSSearch(MazeMap mazeMap, MapPoint currentPos) {
		super();
		this.mazeMap = mazeMap;
		this.currentPos = currentPos;
	}

	/**
	 * @param mazeMap
	 * @param currentPos
	 * @param agentHelper
	 */
	public BFSSearch(MazeMap mazeMap, MapPoint currentPos,
			AgentHelper agentHelper) {
		super();
		this.mazeMap = mazeMap;
		this.currentPos = currentPos;
		this.agentHelper = agentHelper;
	}

	@Override
	public List<MapPoint> findPath(MapPoint startPoint, MapPoint destination){
		List<MapPoint> path = find_Path(startPoint, destination);
		int wallCounter = 0;
		int treeCounter = 0;
		int doorCounter = 0;
		
		if(path == null)
			return null;
		
		for(Iterator<MapPoint> iterator = path.iterator(); iterator.hasNext();){
			MapPoint point = iterator.next();

			switch(point.getCost()){
			case 1:
				if (mazeMap.getTreePositions().contains(point)) {
					if (!this.getAgentHelper().isHave_axe()) {
//						if (mazeMap.getAxePosition() != null) {
//							path = search_Path(point, mazeMap.getAxePosition());
//							path.addAll(search_Path(mazeMap.getAxePosition(), destination));
//						} else if(this.getAgentHelper().getNum_dynamites_held() < ++treeCounter){
//							// the agent do not have axe, don't know where the axe is, and don't have enough dynamite
//							if(mazeMap.getDynamitePositions().size() > treeCounter){
//								path = search_Path(startPoint, mazeMap.getDynamitePositions().get(0));
//								path.addAll(search_Path(mazeMap.getDynamitePositions().get(0), destination));
//							} else{
								return null;
//							}
//						}
					}
					break;
				} else if (mazeMap.getDoorPositions().contains(point)) {
					if (!this.getAgentHelper().isHave_key()) {
//						if (mazeMap.getKeyPosition() != null) {
//							path = search_Path(startPoint, mazeMap.getKeyPosition());
//							path.addAll(search_Path(mazeMap.getKeyPosition(), destination));
//						} else if(this.getAgentHelper().getNum_dynamites_held() < ++doorCounter){
//							// the agent do not have axe, don't know where the axe is, and don't have dynamite
//							if(mazeMap.getDynamitePositions().size() > doorCounter){
//								path = search_Path(startPoint, mazeMap.getDynamitePositions().get(0));
//								path.addAll(search_Path(mazeMap.getDoorPositions().get(0), destination));
//							}else {
								return null;
//							}
//						}
					}
					break;
				}
			case 2:
				if (this.getAgentHelper().getNum_dynamites_held() < ++wallCounter) {
//					if (this.getAgentHelper().getNum_dynamites_held() < 0) {
//					if (mazeMap.getDynamitePositions().size() > wallCounter) {
//						// This can be change to find the nearest dynamite instead
//						path = search_Path(startPoint, mazeMap.getDynamitePositions().get(0));
//						path.addAll(search_Path(mazeMap.getDoorPositions().get(0), destination));
//					} else {
						return null;
//					}
				} 
				break;
			}
		}
		return path;
	}
	
	public List<MapPoint> find_Path(MapPoint startPoint, MapPoint destination) {

		int[] dx = { 1, -1, 0, 0 }, dy = { 0, 0, 1, -1 };
		Queue<MapPoint> bfsQueue = new LinkedList<MapPoint>();
		List<MapPoint> visitedPoint = new ArrayList<MapPoint>();
		List<MapPoint> obst = this.getMazeMap().getObstaclePositions();
		// Generate path to go form startPoint to destination with Manhattan steps
		List<MapPoint> path = goManhattan(startPoint, destination);
		
		if(path == null){
		
			MapPoint[][] parentPoint = new MapPoint[mazeMap.getInternalMap().length][mazeMap.getInternalMap()[0].length];
			for (int i = 0; i < parentPoint.length; i++) {
				for (int j = 0; j < parentPoint[0].length; j++) {
					parentPoint[i][j] = new MapPoint();
				}
			}
	
			MapPoint currentPoint = startPoint;
	
			bfsQueue.offer(currentPoint);
			visitedPoint.add(currentPoint);
	
			while (!bfsQueue.isEmpty()) {
				currentPoint = bfsQueue.poll();
	
				for (int i = 0; i < 4; i++) {
					MapPoint nextPoint = new MapPoint();
	
					// Check if currentPoint is a valid point
					if (currentPoint.getRow() + dx[i] >= 0
							&& currentPoint.getColumn() + dy[i] >= 0) {
	
						nextPoint.setRow(currentPoint.getRow() + dx[i]);
						nextPoint.setColumn(currentPoint.getColumn() + dy[i]);
						nextPoint.setCost(currentPoint.getCost());
	
						// Check if nextPoint is valid
						if (nextPoint.getRow() < parentPoint.length
								&& nextPoint.getColumn() < parentPoint[0].length) {
	
							if (nextPoint.equals(destination)) {
								parentPoint[nextPoint.getRow()][nextPoint.getColumn()].setRow(currentPoint.getRow());
								parentPoint[nextPoint.getRow()][nextPoint.getColumn()].setColumn(currentPoint.getColumn());
								parentPoint[nextPoint.getRow()][nextPoint.getColumn()].setCost(nextPoint.getCost());
								break;
							}
	
							if (!visitedPoint.contains(nextPoint)) {
								if (!obst.contains(nextPoint)) {
									parentPoint[nextPoint.getRow()][nextPoint.getColumn()].setCost(0);
								} else if (this.getMazeMap().getTreePositions().contains(nextPoint)
										|| this.getMazeMap().getDoorPositions().contains(nextPoint)) {
									parentPoint[nextPoint.getRow()][nextPoint.getColumn()].setCost(1);
								} else if (this.getMazeMap().getWallPositions().contains(nextPoint)) {
									parentPoint[nextPoint.getRow()][nextPoint.getColumn()].setCost(2);
								} else if (this.getMazeMap().getWaterPositions().contains(nextPoint)) {
									parentPoint[nextPoint.getRow()][nextPoint.getColumn()].setCost(3);
								}
								parentPoint[nextPoint.getRow()][nextPoint.getColumn()].setRow(currentPoint.getRow());
								parentPoint[nextPoint.getRow()][nextPoint.getColumn()].setColumn(currentPoint.getColumn());
	
								visitedPoint.add(nextPoint);
								bfsQueue.offer(nextPoint);
							}
						}
					}
				}
			}
//			for (int w = 0; w < parentPoint.length; w++) {
//				for (int y = 0; y < parentPoint[0].length; y++) {
//					System.out.print(parentPoint[w][y].toString());
//				}
//				System.out.println();
//			}
			for(int i =0; i< 3; ++i){
				path = convertResult(generateMaps(parentPoint, i), destination);
//				int j = getManhattanDistance(startPoint, destination);
//				System.out.print(' ');
				if(path.size() >= getManhattanDistance(startPoint, destination)){
					break;
				} else{
					path = null;
				}
			}
			
		}
		return path;

	}
	
	// Convert parent MapPoint into path
	public List<MapPoint> convertResult(MapPoint[][] mapPoints, MapPoint goal) {
		List<MapPoint> path = new ArrayList<MapPoint>();

		path.add(goal);
		int i = goal.getRow();
		int j = goal.getColumn();
		int k = 1;
		int m = 0;
		int n = 0;
		while (i != 0 || j != 0) {
			m = i;
			n = j;	
			path.add(k, mapPoints[m][n]);
			i = mapPoints[m][n].getRow();
			j = mapPoints[m][n].getColumn();		
			k++;
		}
//		Collections.reverse(path);
//		path.remove(0);
		path.remove(path.size() - 1);
		return path;		
	}

	// Generate necessary actions according to input path
	public List<Character> generateActions(List<MapPoint> path, Direction direction) {
		List<Character> actions = new ArrayList<Character>();
		MapPoint point = path.get(0);

		for (Iterator<MapPoint> iterator = path.iterator(); iterator.hasNext();) {
			MapPoint nextPoint = iterator.next();
			if(!point.equals(nextPoint)){
			if(nextPoint.isDownOf(point)){
				actions.addAll(changeDiection(direction, Direction.South));
			} else if(nextPoint.isLeftOf(point)){
				actions.addAll(changeDiection(direction, Direction.West));
			} else if(nextPoint.isRightOf(point)){
				actions.addAll(changeDiection(direction, Direction.East));
			} else if(nextPoint.isUpOf(point)){
				actions.addAll(changeDiection(direction, Direction.North));
			}
			
			if(nextPoint.getCost() > 0 && nextPoint.getCost() < 3){
				List<Character> list = processObstacle(nextPoint);
				actions.addAll(list);
			}
			
			actions.add('F');
			point = nextPoint;
			}
		}
		return actions;
	}

	// Check and handle obstacle in input position
	public List<Character> processObstacle(MapPoint position) {
		MazeMap map = this.getMazeMap();
		List<Character> actions = new ArrayList<Character>();

		switch(position.getCost()){
			case 1:
				if (map.getTreePositions().contains(position)) {
						actions.add('C');
				} else if (map.getDoorPositions().contains(position)) {
						actions.add('O');
				}
				break;
			case 2:
					actions.add('B');
				break;
		}
		
		return actions;
	}
	
	// Generate necessary actions to change the agent's current direction to target direction
	public List<Character> changeDiection(Direction currentDirection, Direction targetDirection){
		
		List<Character> list = new ArrayList<Character>();
		
		switch(targetDirection.value() - currentDirection.value()){
			case -3:
				list.add('L');
				break;
			case -2:
				list.add('R');
				list.add('R');
				break;
			case -1:
				list.add('R');
				break;
			case 1:
				list.add('L');
				break;
			case 2:
				list.add('R');
				list.add('R');
				break;
			case 3:
				list.add('R');
				break;
		}
		return list;
	}
	
	// Generate different maps with different cost
	public MapPoint[][] generateMaps(MapPoint[][] map, int cost){
		
		MapPoint[][] mapWithCost = new MapPoint[map.length][map[0].length]; 
		
		for(int i = 0; i < map.length; i++){
			for(int j = 0; j < map[0].length; j++){
				mapWithCost[i][j] = new MapPoint();
				switch(cost){
					case 0:
						if(map[i][j].getCost() == 0) {
							mapWithCost[i][j] = map[i][j];
						}
					break;
					case 1:
						if(map[i][j].getCost() <= 1 ) {
							mapWithCost[i][j] = map[i][j];
						}
					break;
					case 2:
						if(map[i][j].getCost() <= 2) {
							mapWithCost[i][j] = map[i][j];
						}
					break;
				}
			}
		}		
		return mapWithCost;
	}
	
	// Generate path going right-angle from startPoint to destination
	public List<MapPoint> goManhattan(MapPoint startPoint, MapPoint destination) {
		List<MapPoint> manhattanPath1 = new ArrayList<MapPoint>();
		List<MapPoint> manhattanPath2 = new ArrayList<MapPoint>();
		List<MapPoint> obst = this.getMazeMap().getObstaclePositions();
		
		int verticalLength = Math.abs(startPoint.getRow() - destination.getRow());
		int horizontalLength = Math.abs(startPoint.getColumn() - destination.getColumn());
		
		MapPoint p = this.getMazeMap().getInternalMap()[destination.getRow()][startPoint.getColumn()];
		MapPoint m = this.getMazeMap().getInternalMap()[startPoint.getRow()][destination.getColumn()];
		
		switch (startPoint.checkRelativePosition(destination)) {
		case North:
			manhattanPath1.addAll(generateVerticalPath(startPoint, verticalLength, -1));
			break;
		case South:
			manhattanPath1.addAll(generateVerticalPath(startPoint, verticalLength, 1));
			break;
		case West:
			manhattanPath1.addAll(generateHorizontalPath(startPoint, horizontalLength, -1));
			break;
		case East:
			manhattanPath1.addAll(generateHorizontalPath(startPoint, horizontalLength, 1));
			break;
		case NorthEast:
			if(p == null){
				manhattanPath1 = null;
			} else {
				manhattanPath1.addAll(generateVerticalPath(startPoint, verticalLength, -1));
				manhattanPath1.addAll(generateHorizontalPath(p, horizontalLength, 1));
			}

			if(m == null){
				manhattanPath2 = null;
			} else {
				manhattanPath2.addAll(generateHorizontalPath(startPoint, horizontalLength, 1));
				manhattanPath2.addAll(generateVerticalPath(m, verticalLength, -1));
			}
			break;
		case NorthWest:
			if(p == null){
				manhattanPath1 = null;
			} else {
				manhattanPath1.addAll(generateVerticalPath(startPoint, verticalLength, -1));
				manhattanPath1.addAll(generateHorizontalPath(p, horizontalLength, -1));
			}
			if(m == null){
				manhattanPath2 = null;
			} else {
				manhattanPath2.addAll(generateHorizontalPath(startPoint, horizontalLength, -1));
				manhattanPath2.addAll(generateVerticalPath(m, verticalLength, -1));
			}
			break;
		case SouthEast:
			if(p == null){
				manhattanPath1 = null;
			} else {
				manhattanPath1.addAll(generateVerticalPath(startPoint, verticalLength, 1));
				manhattanPath1.addAll(generateHorizontalPath(p, horizontalLength, 1));
			}
			if(m == null){
				manhattanPath2 = null;
			} else {
				manhattanPath2.addAll(generateHorizontalPath(startPoint, horizontalLength, 1));
				manhattanPath2.addAll(generateVerticalPath(m, verticalLength, 1));
			}
			break;
		case SouthWest:
			if(p == null){
				manhattanPath1 = null;
			} else {
				manhattanPath1.addAll(generateVerticalPath(startPoint, verticalLength, 1));
				manhattanPath1.addAll(generateHorizontalPath(p, horizontalLength, -1));
			}

			if(m == null){
				manhattanPath2 = null;
			} else {
				manhattanPath2.addAll(generateHorizontalPath(startPoint, horizontalLength, -1));
				manhattanPath2.addAll(generateVerticalPath(m, verticalLength, 1));
			}
			break;
		}
		
		if(!(manhattanPath1 == null)){
			Collections.reverse(manhattanPath1);
			for(Iterator<MapPoint> iterator = manhattanPath1.iterator(); iterator.hasNext();){
				if (obst.contains(iterator.next())) {
					return null;
				}
			}
		}

		if(!(manhattanPath2 == null)){
			Collections.reverse(manhattanPath2);

			for(Iterator<MapPoint> iterator = manhattanPath2.iterator(); iterator.hasNext();){
				if(obst.contains(iterator.next())){
					return null;
				}
			}
		}
		return manhattanPath1;
	}
	
	// Calculate Manhattan Distance between two points
	public int getManhattanDistance(MapPoint startPoint, MapPoint endPoint) {
		return (Math.abs(startPoint.getRow() - endPoint.getRow()) + Math
				.abs(startPoint.getColumn() - endPoint.getColumn()));
	}
	
	// Generate path from startPoint to a MapPoint in the same column
	public List<MapPoint> generateVerticalPath(MapPoint startPoint, int steps, int direction){
		List<MapPoint> path = new ArrayList<MapPoint>();
	
		switch(direction){
		case 1:
			for(int i = 1; i <= steps; i ++){
				path.add(this.getMazeMap().getInternalMap()[startPoint.getRow() + i][startPoint.getColumn()]);
			}			
			break;
		case -1:
			for(int i = 1; i <= steps; i ++){
				path.add(this.getMazeMap().getInternalMap()[startPoint.getRow() - i][startPoint.getColumn()]);
			}
			break;
		}
		
		return path;
	}
	
	// Generate path from startPoint to a MapPoint in the same row
	public List<MapPoint> generateHorizontalPath(MapPoint startPoint, int steps, int direction){
		List<MapPoint> path = new ArrayList<MapPoint>();
		
		switch(direction){
		case 1:
			for(int i = 1; i <= steps; i ++){
				path.add(this.getMazeMap().getInternalMap()[startPoint.getRow()][startPoint.getColumn() + i]);
			}			
			break;
		case -1:
			for(int i = 1; i <= steps; i ++){
				path.add(this.getMazeMap().getInternalMap()[startPoint.getRow()][startPoint.getColumn() - i]);
			}
			break;
		}

		return path;
	
	}
}
