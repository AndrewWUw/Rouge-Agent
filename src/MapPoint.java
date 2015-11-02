/**************************************
 * MapPoint.java 
 * A single point location in the maze
 * 
 * Author: Andrew WU
 * Date: 19-05-2013
 * 
 *************************************/
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MapPoint implements Comparable<MapPoint>{

	private MazeMap map;
	private int row;
	private int column;
	private char content;
	private boolean isVisited;
	private boolean visited = false;
	private int cost;

	// Path search related fields.
	private MapPoint parent;	// the parent point through which we got to here.
	private int gCost; 			// cost of getting from start to this point.
	private int hCost;			// estimated cost of the cheapest path from this point to goal.
	private int fCost;			// estimated total cost of cheapest solution through this point
	private int dynamiteHeld;	// number of dynamites held up to this point
	private int dynamiteUsed;	// number of dynamites required to reach this point
	
	public int getDynamiteUsed() {
		return dynamiteUsed;
	}

	public void setDynamiteUsed(int dynamiteUsed) {
		this.dynamiteUsed = dynamiteUsed;
	}
	
	public int getDynamiteHeld() {
		return dynamiteHeld;
	}

	public void setDynamiteHeld(int dynamiteHeld) {
		this.dynamiteHeld = dynamiteHeld;
	}

	public int getgCost() {
		return gCost;
	}

	public void setgCost(int gCost) {
		this.gCost = gCost;
	}

	public int gethCost() {
		return hCost;
	}

	public void sethCost(int hCost) {
		this.hCost = hCost;
	}

	public int getfCost() {
		return fCost;
	}

	public void setfCost(int fCost) {
		this.fCost = fCost;
	}

	public MapPoint() {
		this.row = 0;
		this.column = 0;
		isVisited = false;
		this.cost = 4;
	}
	
//	public MapPoint(int row, int column) {
//		super();
//		this.row = row;
//		this.column = column;
//		this.cost = 4;
//	}
	
	/**
	 * @param row
	 * @param column
	 */
	public MapPoint(MazeMap map, int row, int column) {
		super();
		this.map = map;
		this.row = row;
		this.column = column;
		this.cost = 4;
	}
	
	public MapPoint(MazeMap map, int row, int column, char content) {
	this(map, row, column);
		this.content = content;
		switch(content){
		case 'd':
			this.cost = 0;
			break;
		case 'k':
			this.cost = 0;
			break;
		case 'a':
			this.cost = 0;
			break;
		case 'g':
			this.cost = 0;
			break;
		case 'T':
			this.cost = 1;
			break;
		case  '-':
			this.cost = 1;
			break;
		case '*':
			this.cost = 2;
			break;
		case '~':
			this.cost = 3;
			break;
		default:
			this.cost = 4;
			break;
		}
	}
	
	public void setPoint(int row, int column) {
		this.row = row;
		this.column = column;
	}
	
	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}
	
	public char getContent() {
		return content;
	}

	public void setContent(char content) {
		this.content = content;
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}
	
	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public MapPoint getParent() {
		return parent;
	}

	public void setParent(MapPoint parent) {
		this.parent = parent;
	}
	
    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
            return false;
        MapPoint point = (MapPoint) obj;
        return (this.row == point.getRow() && this.column == point.getColumn());
    }

    @Override
    public int hashCode()
    {
        return (row * 5000)  + column;
    }
	
	public MapPoint getNeighbor(Direction direction) {
		switch (direction) {
		case North:
			return map.getAt(row - 1, column);
		case East:
			return map.getAt(row, column + 1);
		case South:
			return map.getAt(row + 1, column);
		case West:
			return map.getAt(row, column - 1);	
		}
		return null;
	}
	
	public List<MapPoint> getNeighbors() {
		List<MapPoint> neighbors = new ArrayList<MapPoint>();
		for (int i = Direction.South.value(); i >= Direction.East.value(); i--) {
			MapPoint neighbor = getNeighbor(Direction.fromValue(i));
			if (neighbor != null) {
				neighbors.add(neighbor);
			}
		}
		return neighbors;
	}
	
	public boolean isLeftOf(MapPoint p) {
		return this.row == p.getRow() && this.column == p.getColumn() - 1;
	}
	
	public boolean isRightOf(MapPoint p) {
		return this.row == p.getRow() && this.column == p.getColumn() + 1;
	}
	
	public boolean isUpOf(MapPoint p) {
		return this.row == p.getRow() - 1 && this.column == p.getColumn();
	}
	
	public boolean isDownOf(MapPoint p) {
		return this.row == p.getRow() + 1 && this.column == p.getColumn();
	}

	public boolean isWall() {
		return content == '*';
	}
	
	public boolean isTree() {
		return content == 'T';
	}
	
	public boolean isDoor() {
		return content == '-';
	}
	
	public boolean isDynamite() {
		return content == 'd';
	}
	
	public boolean isAxe() {
		return content == 'a';
	}
	
	public boolean isKey() {
		return content == 'k';
	}

    // Used to order map points in a priority queue. 
	// If a point requires less dynamite to reach or, once reached, it accumulates more dynamite, or it's f cost is smaller, then it has higher priority.
    public int compareTo(MapPoint otherPoint)
    {
        if(this.dynamiteUsed < otherPoint.getDynamiteUsed())
            return -1;

        if (this.dynamiteUsed == otherPoint.getDynamiteUsed()) {
            if(this.dynamiteHeld > otherPoint.getDynamiteHeld()) {
                return -1;
            }
            else if (this.dynamiteHeld == otherPoint.getDynamiteHeld()) {
                return this.fCost - otherPoint.getfCost();
            }
        }
        return 1;
    }
    
    public boolean costLessRegardlessHCost(MapPoint otherPoint) {
        if(this.dynamiteUsed < otherPoint.getDynamiteUsed())
            return true;

        if (this.dynamiteUsed == otherPoint.getDynamiteUsed()) {
            if(this.dynamiteHeld > otherPoint.getDynamiteHeld()) {
                return true;
            }
            else if (this.dynamiteHeld == otherPoint.getDynamiteHeld()) {
                return this.gCost < otherPoint.getgCost();
            }
        }
        return false;
    }
    
 	public Direction checkRelativePosition(MapPoint point) {

		if (this.getRow() >= point.getRow()
				&& this.getColumn() > point.getColumn()) {
			if (this.getRow() - point.getRow() == 0) {
				return Direction.West;
			} else {
				return Direction.NorthWest;
			}
		} else if (this.getRow() > point.getRow()
				&& this.getColumn() <= point.getColumn()) {
			if (this.getColumn() - point.getColumn() == 0) {
				return Direction.North;
			} else {
				return Direction.NorthEast;
			}
		} else if (this.getRow() <= point.getRow()
				&& this.getColumn() < point.getColumn()) {
			if (this.getRow() - point.getRow() == 0) {
				return Direction.East;
			} else {
				return Direction.SouthEast;
			}
		} else if (this.getRow() < point.getRow()
				&& this.getColumn() >= point.getColumn()) {
			if (this.getColumn() - point.getColumn() == 0) {
				return Direction.South;
			} else {
				return Direction.SouthWest;
			}
		}
		return null;
 	}
 	
	@Override
	public String toString() {
		return "[(" + row + ", " + column + ") cost=" + cost + " content=" + content + "]";
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}
	
	// If the current point is a dynamite, return a list of dynamites order by distance to current location.
	public List<MapPoint> getNeighboringDynamites() {
		List<MapPoint> dynamites = map.getDynamitePositions();
		Collections.sort(dynamites, new DynamiteDistanceComparator());
		//dynamites.remove(0);
		return dynamites;
	}
	
	// Calculate Manhattan Distance between two points
	public int getManhattanDistance(MapPoint endPoint) {
		return (Math.abs(this.getRow() - endPoint.getRow()) + Math
				.abs(this.getColumn() - endPoint.getColumn()));
	}
	
	private class DynamiteDistanceComparator implements Comparator<MapPoint> {
		@Override
		public int compare(MapPoint d1, MapPoint d2) {

			int manhattan0 = (Math.abs(d1.getRow() - row) + Math.abs(d1.getColumn() - column));
			int manhattan1 = (Math.abs(d2.getRow() - row) + Math.abs(d2.getColumn() - column));
			
			return manhattan0 - manhattan1;
		}
		
	}
}
