/**************************************
 * MazeMap.java
 * THe local maze map the agent stored
 * 
 * Author: Andrew WU
 * Date: 19-05-2013
 * 
 *************************************/
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MazeMap {
	final static ArrayList<Character> Obstacle = new ArrayList<Character>(Arrays.asList('T', '-', '*', '~'));
	private final static char Boundary = '~';

	private MapPoint[][] map;

	private MapPoint position;
	private MapPoint goldPosition;
	private MapPoint axePosition;
	private MapPoint keyPosition;
	private List<MapPoint> dynamitePositions;

	private List<MapPoint> obstaclePositions;
	private List<MapPoint> treePositions;
	private List<MapPoint> wallPositions;
	private List<MapPoint> doorPositions;
	private List<MapPoint> waterPositions;

	//===============================
	// Constructor
	//===============================
	public MazeMap(char[][] initialView) {
		// Create map.
		this.map = new MapPoint[initialView.length][initialView[0].length];
		for (int i = 0; i < initialView.length; i++) {
			for (int j = 0; j < initialView[0].length; j++) {
				if (i == 2 && j == 2) {
					map[i][j] = new MapPoint(this, i, j, ' ');
					map[i][j].setVisited(true);
				}
				else
					map[i][j] = new MapPoint(this, i, j, initialView[i][j]);
			}
		}
		
		// Scan map details.
		this.dynamitePositions = new ArrayList<MapPoint>();
		this.obstaclePositions = new ArrayList<MapPoint>();
		this.treePositions = new ArrayList<MapPoint>();
		this.wallPositions = new ArrayList<MapPoint>();
		this.waterPositions = new ArrayList<MapPoint>();
		this.doorPositions = new ArrayList<MapPoint>();
		
		scanMapDetails();
	}
	
	public void scanMapDetails() {
		this.dynamitePositions.clear();
		this.obstaclePositions.clear();
		this.treePositions.clear();
		this.wallPositions.clear();
		this.waterPositions.clear();
		this.doorPositions.clear();
		
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < this.map[0].length; j++) {
				MapPoint point = map[i][j];
				if (point == null)
					continue;
				
				switch (point.getContent()) {
				case 'T':
					this.treePositions.add(point);
					this.obstaclePositions.add(point);
					map[i][j].setCost(1);
					break;
				case '-':
					this.doorPositions.add(point);
					this.obstaclePositions.add(point);
					map[i][j].setCost(1);
					break;
				case '*':
					this.wallPositions.add(point);
					this.obstaclePositions.add(point);
					map[i][j].setCost(2);
					break;
				case '~':
					this.waterPositions.add(point);
					this.obstaclePositions.add(point);
					map[i][j].setCost(3);
					break;
				case 'a':
					this.setAxePosition(point);
					map[i][j].setCost(0);
					break;
				case 'k':
					this.setKeyPosition(point);
					map[i][j].setCost(0);
					break;
				case 'd':
					this.dynamitePositions.add(point);
					map[i][j].setCost(0);
					break;
				case 'g':
					this.setGoldPosition(point);
					map[i][j].setCost(0);
					break;
				}
			}
		}
	}

	//===============================
	// Public Methods
	//===============================
	private void expandMap(MapPoint agentPoint, Direction direction, char newView[][]) {
		System.out.println("Expanding map...");
		MapPoint[][] newMap = null;
		
		switch (direction) {
		case North:
			newMap = new MapPoint[map.length + 1][map[0].length];
			for (int i = 0; i < newView.length; i++) {
				// Add the first row in the new view as the first row in the new map.
				int c = agentPoint.getColumn();
				newMap[0][c - 2 + i] = new MapPoint(this, 0, c - 2 + i, newView[0][i]); 
			}
			
			// Add rest of map.
			for (int i = 0; i < map.length; i++) {
				for (int j = 0; j < map[0].length; j++) {
					MapPoint p = map[i][j];
					if (p != null) {
						p.setRow(p.getRow() + 1); // Increment row number
						newMap[i+1][j] = p;
					}
				}
			}
			break;
			
		case South:
			newMap = new MapPoint[map.length + 1][map[0].length];
			for (int i = 0; i < newView.length; i++) {
				//Add the first row in the new view as the last row in the new map.
				int c = agentPoint.getColumn();
				newMap[newMap.length - 1][c + 2 - i] = new MapPoint(this, newMap.length - 1, c + 2 -i, newView[0][i]); 
			}
			
			// Add rest of map.
			for (int i = 0; i < map.length; i++) {
				for (int j = 0; j < map[0].length; j++) {
					newMap[i][j] = map[i][j];
				}
			}
			break;
			
		case East:
			newMap = new MapPoint[map.length][map[0].length + 1];
			for (int i = 0; i < newView.length; i++) {
				//Add the first row in the new view as the last column in the new map.
				int r = agentPoint.getRow();
				newMap[r - 2 + i][newMap[0].length - 1] = new MapPoint(this, r - 2 + i, newMap[0].length - 1, newView[0][i]);
			}
			
			// Add rest of map.
			for (int i = 0; i < map.length; i++) {
				for (int j = 0; j < map[0].length; j++) {
					newMap[i][j] = map[i][j];
				}
			}
			break;
			
		case West:
			newMap = new MapPoint[map.length][map[0].length + 1];
			for (int i = 0; i < newView.length; i++) {
				//Add the first row in the new view as the first column in the new map.
				int r = agentPoint.getRow();
				newMap[r + 2 - i][0] = new MapPoint(this, r + 2 - i, 0, newView[0][i]); 
			}
			
			// Add rest of map.
			for (int i = 0; i < map.length; i++) {
				for (int j = 0; j < map[0].length; j++) {
					MapPoint p = map[i][j];
					if (p != null) {
						p.setColumn(p.getColumn() + 1); // Increment column number
						newMap[i][j+1] = p;
					}
				}
			}
			break;
		}
		// Swap over.
		map = newMap;
	}
	
	public MapPoint updateMap(MapPoint agentPosition, Direction direction, char newView[][]) {
		boolean updated = false;
		MapPoint agentNewPosition = agentPosition;
		switch (direction) {
		case North:
			if (agentPosition.getRow() <= 2) {
				expandMap(agentPosition, direction, newView);
				updated = true;
			}
			agentNewPosition = agentPosition.getNeighbor(Direction.North);
			break;
		case South:
			agentNewPosition = agentPosition.getNeighbor(Direction.South);
			if (agentNewPosition.getRow() + 2 > map.length - 1) {
				expandMap(agentNewPosition, direction, newView);
				updated = true;
			}
			break;
		case East:
			agentNewPosition = agentPosition.getNeighbor(Direction.East);
			if (agentNewPosition.getColumn() + 2 > map[0].length - 1) {
				expandMap(agentNewPosition, direction, newView);
				updated = true;
			}
			break;
		case West:
			if (agentPosition.getColumn() <= 2 ) {
				expandMap(agentPosition, direction, newView);
				updated = true;
			}
			agentNewPosition = agentPosition.getNeighbor(Direction.West);
			break;
		}
		if (!updated)
			updateExploredPoints(agentNewPosition, direction, newView);
		
		// Scan map for new tools and features.
		scanMapDetails();
		
		return agentNewPosition;
	}

	private void updateExploredPoints(MapPoint agentPoint, Direction direction, char[][] newView) {
		for (int i = 0; i < newView.length; i++) {
			int r = 0, c = 0;
			switch (direction) {
			case North:
				r = agentPoint.getRow() - 2;
				c = agentPoint.getColumn() - 2 + i;
				break;
			case South:
				r = agentPoint.getRow() + 2; 
				c = agentPoint.getColumn() + 2 - i;
				break;
			case East:
				r = agentPoint.getRow() - 2 + i; 
				c = agentPoint.getColumn() + 2;
				break;
			case West:
				r = agentPoint.getRow() + 2 - i; 
				c = agentPoint.getColumn() - 2;
				break;
			}
			if (map[r][c] == null)
				map[r][c] = new MapPoint(this, r, c, newView[0][i]);
		}
	}
	
	private boolean withinBounds(int row, int col) {
		return row < map.length && row >= 0 && 
				col < map[0].length && col >= 0;
	}
	
	public MapPoint getAt(int row, int col) {
		if (!withinBounds(row, col))
			return null;
		
		MapPoint point = this.map[row][col];
		if (point != null) {
			// Check for corruption.
			if (point.getRow() != row || point.getColumn() != col)
				try {
					throw new Exception(String.format("Invalid point found on map at [%1$s][%2$s] and the point's coord is (%3$s,%4$s)",
							row, col, point.getRow(), point.getColumn()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			return point;
		}
		return null;
	}

	//===============================
	// AStar search related
	//===============================
	public void cleanupSearch() {
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < this.map[0].length; j++) {
				MapPoint point = map[i][j];
				if (point == null)
					continue;
				
				point.setParent(null);
				point.setgCost(0);
				point.setfCost(0);
				point.sethCost(0);
				point.setDynamiteHeld(0);
				point.setDynamiteUsed(0);
			}
		}
	}

	//===============================
	// Public Properties
	//===============================
	public MapPoint[][] getInternalMap() {
		return map;
	}
	
	public MapPoint getPosition() {
		return position;
	}

	public void setPosition(MapPoint position) {
		this.position = position;
	}

	public MapPoint getGoldPosition() {
		return goldPosition;
	}

	public void setGoldPosition(MapPoint goldPosition) {
		this.goldPosition = goldPosition;
	}

	public MapPoint getAxePosition() {
		return axePosition;
	}

	public void setAxePosition(MapPoint axePosition) {
		this.axePosition = axePosition;
	}

	public MapPoint getKeyPosition() {
		return keyPosition;
	}

	public void setKeyPosition(MapPoint keyPosition) {
		this.keyPosition = keyPosition;
	}

	public List<MapPoint> getDynamitePositions() {
		return dynamitePositions;
	}

	public void setDynamitePosition(List<MapPoint> dynamitePosition) {
		this.dynamitePositions = dynamitePosition;
	}

	public List<MapPoint> getObstaclePositions() {
		return obstaclePositions;
	}

	public void setObstaclePositions(List<MapPoint> obstaclePositions) {
		this.obstaclePositions = obstaclePositions;
	}

	public void addObstaclePositions(MapPoint position) {
		this.obstaclePositions.add(position);
	}

	public List<MapPoint> getTreePositions() {
		return treePositions;
	}

	public void setTreePositions(List<MapPoint> treePositions) {
		this.treePositions = treePositions;
	}

	public List<MapPoint> getWallPositions() {
		return wallPositions;
	}

	public void setWallPositions(List<MapPoint> wallPositions) {
		this.wallPositions = wallPositions;
	}

	public List<MapPoint> getDoorPositions() {
		return doorPositions;
	}

	public void setDoorPositions(List<MapPoint> doorPositions) {
		this.doorPositions = doorPositions;
	}

	public List<MapPoint> getWaterPositions() {
		return waterPositions;
	}

	public void setWaterPositions(List<MapPoint> waterPositions) {
		this.waterPositions = waterPositions;
	}
}
