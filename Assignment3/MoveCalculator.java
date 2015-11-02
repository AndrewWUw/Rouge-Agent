import java.util.*;

public class MoveCalculator
{
    //returns the sequence of moves to get between adjacent squares
    //start and next must be adjacent
    public static List<Character> getSingleMove(char agentFacingDirection, GameMap map, Coords start, Coords next)
    {
        //Calculate the direction we need to face
        List<Character> moves = new ArrayList<Character>();
        Character needToFace = 'd';
        if (next.getX() - start.getX() == 1)
            needToFace = 'd';
        else if (next.getX() - start.getX() == -1)
            needToFace = 'u';
        else if (next.getY() - start.getY() == 1)
            needToFace = 'r';
        else if (next.getY() - start.getY() == -1)
            needToFace = 'l';

        String diff = Character.toString(agentFacingDirection).concat(needToFace.toString());

        if (diff.equals("ur") || diff.equals("rd") || diff.equals("dl") || diff.equals("lu"))
        {
            moves.add('r');
        }
        else if (diff.equals("ru") || diff.equals("ul") || diff.equals("ld") || diff.equals("dr"))
        {
            moves.add('l');
        }
        else if (diff.equals("du") || diff.equals("ud") || diff.equals("lr") || diff.equals("rl"))
        {
            moves.add('l');
            moves.add('l');
        }

        //Chop/blast if necessary
        if (map.isTree(next))
            moves.add('c');
        if (map.isWall(next))
            moves.add('b');

        moves.add('f');

        return moves;
    }

    public static boolean validMoveSquare(GameMap map, Coords c, boolean inBoat, boolean hasAxe, boolean hasDynamite)
    {
        if (map.isLand(c) || map.isDynamite(c) || map.isAxe(c) || map.isGold(c) || map.isBoat(c))
            return true;
        if (map.isWall(c))
            return hasDynamite;
        if (map.isSea(c))
            return inBoat;
        if (map.isTree(c))
            return hasAxe;
        else return false;
    }

    public static List<Coords> getAStarPathToCoords(GameMap map, AgentInfo agentInfo, Coords goal)
    {
        //If in the same area just do astar
        return AStarPath(map, agentInfo, goal);
    }

    private static int manhattanHeuristic(Coords a, Coords b)
    {
        return (Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY()));
    }

    private static List<Coords> AStarPath(GameMap map, AgentInfo agentInfo, Coords goal)
    {
        //Astar to find the clear path between spots
        PriorityQueue<AStarUnit> openSet = new PriorityQueue<AStarUnit>(20, new AStarComparator());

        int gScore = 0;
        int hScore = manhattanHeuristic(agentInfo.getCoords(), goal);
        int fScore = gScore + hScore;
        AStarUnit node = new AStarUnit(gScore, hScore, fScore, agentInfo.getCoords(), null, agentInfo.dynamiteHeld(), 0, agentInfo.inBoat());
        openSet.add(node);
        List<Integer> closedSet = new ArrayList<Integer>();

        while (!openSet.isEmpty())
        {
            AStarUnit current = openSet.poll();
            if (current.getCoords().equals(goal))
            {
                List<Coords> returnPath = new ArrayList<Coords>();
                //Add to list all nodes constructing path
                while (current.getParent() != null)
                {
                    //adds to front of list
                    returnPath.add(0, current.getCoords());
                    current = current.getParent();
                }
                //return constructed path
                return returnPath;
            }

            closedSet.add(current.getCoords().hashCode());
            //For each neighbour, add to openset if not in openset and is visitable
            //or update if in openset and path-to is better from here\
            List<Coords> neighbours = new ArrayList<Coords>();
            neighbours.add(current.getCoords().left());
            neighbours.add(current.getCoords().up());
            neighbours.add(current.getCoords().right());
            neighbours.add(current.getCoords().down());
            for (Coords coords : neighbours)
            {
                if(MoveCalculator.validMoveSquare(map, coords, current.isInBoat(), agentInfo.hasAxe(), (current.getDynamite() > 0)) && !closedSet.contains(coords.hashCode()))
                {
                    int tempGScore = current.getgScore() + 1;
                    boolean isWall = (map.getSquare(coords) == '*');
                    boolean isBoat = (map.getSquare(coords) == 'B');
                    boolean isLand = (map.getSquare(coords) == ' ');
                    boolean isDynamite = (map.getSquare(coords) == 'd');
                    int d = current.getDynamite();
                    int dUsed = current.getDynamiteUsed();

                    //carry out updates to boat and dynamite
                    boolean newInBoat = current.isInBoat();
                    if (current.isInBoat() && isLand)
                    {
                        newInBoat = false;
                    }
                    else if(isBoat)
                    {
                        newInBoat = true;
                    }
                    if(isWall)
                    {
                        d--;
                        dUsed++;
                    }
                    if (isDynamite)
                    {
                        d++;
                    }

                    //Get node if in openset already
                    AStarUnit duplicateNode = null;
                    for (AStarUnit unit : openSet)
                    {
                        if (unit.getCoords().equals(coords))
                            duplicateNode = unit;
                    }

                    //If it hasnt been visited, just add it
                    if (duplicateNode == null)
                    {
                        int h = manhattanHeuristic(coords, goal);
                        int f = gScore + hScore;
                        openSet.add(new AStarUnit(tempGScore, h, f, coords, current, d, dUsed, newInBoat));
                    }
                    //if it has been added to astar, update with new parent if the new path is better
                    else if (duplicateNode.getDynamiteUsed() > dUsed ||
                            (duplicateNode.getDynamiteUsed() == dUsed && duplicateNode.getDynamite() < d) ||
                            (duplicateNode.getDynamiteUsed() == dUsed && duplicateNode.getDynamite() == d && duplicateNode.getgScore() > tempGScore))
                    {
                        duplicateNode.setParent(current);
                        duplicateNode.setgScore(tempGScore);
                        duplicateNode.setfScore(tempGScore + duplicateNode.gethScore());
                        duplicateNode.setDynamite(d);
                        duplicateNode.setDynamiteUsed(dUsed);
                        duplicateNode.setInBoat(newInBoat);
                    }
                }
            }
        }

        return null;
    }

    public static Coords getFloodPathCoords(GameMap map, AgentInfo agentInfo, Coords goal)
    {
        int floodLevel = 1;
        FloodNode start = new FloodNode(map.getFloodMap().clone(), floodLevel, goal, agentInfo.dynamiteHeld(), agentInfo.getCoords());
        //get wall tiles adjacent to this flood level
        List<Coords> nextSteps = map.adjacentToFillLevelList(start.getFloodMap(), start.getFloodLevel());
        //for all of them, return if not null path
        for (Coords next : nextSteps)
        {
            int[][] tempFlood = map.copyMap(start.getFloodMap());
            int newLevel = start.getFloodLevel() + 1;
            tempFlood[next.getX()][next.getY()] = newLevel;
            FloodNode newNode = new FloodNode(tempFlood, newLevel, start.getGoal(), start.getDynamite() - 1, next);
            Coords recursivePath = recursiveFlood(newNode, start.getDynamite(), map);
            if (recursivePath != null)
                return recursivePath;
        }
        return null;
    }

    private static Coords BFStoFloodedLevelOne(FloodNode node, Coords goal)
    {
        List<Integer> closedSet = new LinkedList<Integer>();
        int[][] floodMap = node.getFloodMap();
        Queue<Coords> openSet = new LinkedList<Coords>();
        openSet.add(goal);

        while (!openSet.isEmpty())
        {
            Coords current = openSet.poll();
            if (floodMap[current.getX()][current.getY()] == 2)
            {
                return current;
            }
            closedSet.add(current.hashCode());
            List<Coords> neighbours = new ArrayList<Coords>();
            neighbours.add(current.left());
            neighbours.add(current.up());
            neighbours.add(current.right());
            neighbours.add(current.down());
            for (Coords n : neighbours)
            {
                if (!closedSet.contains(n.hashCode()) && (floodMap[n.getX()][n.getY()] <= (floodMap[current.getX()][current.getY()])) && floodMap[n.getX()][n.getY()] != 0)
                {
                    if (floodMap[n.getX()][n.getY()] == (floodMap[current.getX()][current.getY()] - 1))
                        openSet.clear();
                    openSet.add(n);
                }
            }
        }
        //should never be reached
        return null;
    }

    //starting from a given point, flood into all 0 flooded land tiles
    private static boolean floodFillMap(FloodNode node, Coords coords, GameMap map)
    {
        List<Integer> closedSet = new LinkedList<Integer>();
        int[][] floodMap = node.getFloodMap();
        floodMap[coords.getX()][coords.getY()] = node.getFloodLevel();
        boolean changed = false;

        LinkedList<Coords> floodFillList = coords.neighbours();
        while (!floodFillList.isEmpty())
        {
            Coords current = floodFillList.pop();
            if (!closedSet.contains(current.hashCode()) && floodMap[current.getX()][current.getY()] == 0 && MoveCalculator.validMoveSquare(map, current, false, false, false))
            {
                changed = true;
                closedSet.add(current.hashCode());
                floodMap[current.getX()][current.getY()] = node.getFloodLevel();
                if (map.isDynamite(current))
                    node.setDynamite(node.getDynamite() + 1);
                floodFillList.addAll(current.neighbours());
            }
        }
        return changed;
    }


    private static boolean flooded(FloodNode node)
    {
        int floodLevel = node.getFloodMap()[node.getGoal().getX()][node.getGoal().getY()];
        return floodLevel > 0;
    }

    private static Coords recursiveFlood(FloodNode node, int startingDynamite, GameMap map)
    {
        if (floodFillMap(node, node.getFloodStart(), map))
        {
            //if we have found a target node, return solution
            if (flooded(node) || node.getDynamite() > startingDynamite)
            {
                //return bfs search backwards
                return BFStoFloodedLevelOne(node, node.getFloodStart());
            }
            else if (node.getDynamite() > 0)
            //increase level of flood fill and evaluate next for all wall tiles reachable from this level
            {
                //get wall tiles adjacent to this flood level
                List<Coords> nextSteps = map.adjacentToFillLevelList(node.getFloodMap(), node.getFloodLevel());
                //for all of them, return if not null path
                for (Coords next : nextSteps)
                {
                    int[][] tempFlood = map.copyMap(node.getFloodMap());
                    int newLevel = node.getFloodLevel() + 1;
                    tempFlood[next.getX()][next.getY()] = newLevel;
                    FloodNode newNode = new FloodNode(tempFlood, newLevel, node.getGoal(), node.getDynamite() - 1, next);
                    Coords recursivePath = recursiveFlood(newNode, startingDynamite, map);
                    if (recursivePath != null)
                        return recursivePath;
                }
            }
        }
        //if map does not expand but we still have dynamite, flood again but only to adjacent wall tiles
        else if (node.getDynamite() > 0)
        {
            //get all neighbouring tiles
            List<Coords> neighbours = node.getFloodStart().neighbours();
            for (Coords next : neighbours)
            {
                int[][] tempFlood = map.copyMap(node.getFloodMap());
                int newLevel = node.getFloodLevel();
                if (map.isWall(next) && tempFlood[next.getX()][next.getY()] == 0)
                {
                    tempFlood[next.getX()][next.getY()] = newLevel;
                    FloodNode newNode = new FloodNode(tempFlood, newLevel, node.getGoal(), node.getDynamite() - 1, next);
                    Coords recursivePath = recursiveFlood(newNode, startingDynamite, map);
                    if (recursivePath != null)
                        return recursivePath;
                }
            }
        }
        return null;
    }

    //initialises then calls recursive flood to find longest path
    public static FloodLengthReturn getLongestFloodPathToCoords(GameMap map, AgentInfo agentInfo, Coords goal)
    {
        int floodLevel = 1;
        FloodNode start = new FloodNode(map.getFloodMap().clone(), floodLevel, goal, agentInfo.dynamiteHeld(), agentInfo.getCoords());
        //get wall tiles adjacent to this flood level
        List<Coords> nextSteps = map.adjacentToFillLevelList(start.getFloodMap(), start.getFloodLevel());
        //for all of them, return if not null path
        for (Coords next : nextSteps)
        {
            int[][] tempFlood = map.copyMap(start.getFloodMap());
            int newLevel = start.getFloodLevel() + 1;
            tempFlood[next.getX()][next.getY()] = newLevel;
            FloodNode newNode = new FloodNode(tempFlood, newLevel, start.getGoal(), start.getDynamite() - 1, next);
            FloodLengthReturn recursivePath = recursiveLongestPathFlood(newNode, start.getDynamite(), map);
            if (recursivePath != null)
                return recursivePath;
        }
        return null;
    }

    //Recursively calculates flood path
    private static FloodLengthReturn recursiveLongestPathFlood(FloodNode node, int startingDynamite, GameMap map)
    {
        if (floodFillMap(node, node.getFloodStart(), map))
        {
            //if we have found a target node, return solution
            if (flooded(node) || node.getDynamite() > startingDynamite)
            {
                //return bfs search backwards
                return new FloodLengthReturn(BFStoFloodedLevelOne(node, node.getFloodStart()), node.getFloodLevel());
            }
            else if (node.getDynamite() > 0)
            //increase level of flood fill and evaluate next for all wall tiles reachable from this level
            {
                //get wall tiles adjacent to this flood level
                List<Coords> nextSteps = map.adjacentToFillLevelList(node.getFloodMap(), node.getFloodLevel());
                //for all of them, return if not null path
                for (Coords next : nextSteps)
                {
                    int[][] tempFlood = map.copyMap(node.getFloodMap());
                    int newLevel = node.getFloodLevel() + 1;
                    tempFlood[next.getX()][next.getY()] = newLevel;
                    FloodNode newNode = new FloodNode(tempFlood, newLevel, node.getGoal(), node.getDynamite() - 1, next);
                    FloodLengthReturn recursivePath = recursiveLongestPathFlood(newNode, startingDynamite, map);
                    if (recursivePath != null)
                        return recursivePath;
                }
            }

        }
        else if (node.getDynamite() > 0)
        {
            //get all neighbouring tiles
            List<Coords> neighbours = node.getFloodStart().neighbours();
            for (Coords next : neighbours)
            {
                int[][] tempFlood = map.copyMap(node.getFloodMap());
                int newLevel = node.getFloodLevel();
                if (map.isWall(next) && tempFlood[next.getX()][next.getY()] == 0)
                {
                    tempFlood[next.getX()][next.getY()] = newLevel;
                    FloodNode newNode = new FloodNode(tempFlood, newLevel, node.getGoal(), node.getDynamite() - 1, next);
                    FloodLengthReturn recursivePath = recursiveLongestPathFlood(newNode, startingDynamite, map);
                    if (recursivePath != null)
                        return recursivePath;
                }
            }
        }
        return null;
    }
}
