import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class AIImpl implements AI
{
    Queue<Character> moveList;
    LinkedList<Coords> pathList;
    Explorer explorer;
    AgentInfo agentInfo;
    GameMap map;
    Coords goldLocation;

    AIImpl(AgentInfo agentInfo, GameMap map)
    {
        //list of character moves to output
        moveList = new LinkedList<Character>();
        //list of nodes constituting a path
        pathList = new LinkedList<Coords>();
        //object to control exploration of the map
        explorer = new ExplorerImpl(agentInfo.getCoords(), moveList);
        this.agentInfo = agentInfo;
        this.map = map;
        this.goldLocation = null;
    }

    @Override
    public Character getMove()
    {
        //add area to explore if necessary
        explorer.addExplore(agentInfo, map);

        //execute any current moves waiting first
        if (!moveList.isEmpty())
        {
            return moveList.poll();
        }
        //If there are nodes still to visit  in the current path then do so
        else if (!pathList.isEmpty())
        {
            Coords nextToVisit = pathList.pop();
            moveList.addAll(MoveCalculator.getSingleMove(agentInfo.getDirection(), map, agentInfo.getCoords(), nextToVisit));
            return moveList.poll();
        }
        else
        //AI part - priority of actions
        //Ask AI for next move
        {
            pathList.addAll(MoveCalculator.getAStarPathToCoords(map, agentInfo, getNextAISquare()));
            Coords nextToVisit = pathList.pop();
            moveList.addAll(MoveCalculator.getSingleMove(agentInfo.getDirection(), map, agentInfo.getCoords(), nextToVisit));
            return moveList.poll();
        }
    }

    //get a list of squares to visit from AI rules
    private Coords getNextAISquare()
    {
        //if has gold, then calculate path to start
        if (agentInfo.hasGold())
            return agentInfo.getOrigin();

        //make sure all explorable area is explored
        if (explorer.hasNext())
            return explorer.getNext();

        //next, chop down all trees. Finding a path to gold is expensive and this is cheap, so do this first
        if(agentInfo.hasAxe())
        {
            List<Coords> treeLocation = map.getTreeLocations();
            if (!treeLocation.isEmpty())
                return treeLocation.get(0);
        }

        //if you see gold, check if there is a path to the gold. if so, return it
        //will also return if it ever hits a point where it has greater dynamite than current dynamite
        List<Coords> goldLocation = map.getGoldLocations();
        if (!goldLocation.isEmpty())
        {
            Coords target = MoveCalculator.getFloodPathCoords(map, agentInfo, goldLocation.get(0));
            if (target != null)
                return target;
        }

        //If we have maximised dynamite, then we will accept giving up a dynamite to get the axe
        List<Coords> axeLocation = map.getAxeLocations();
        if (!axeLocation.isEmpty())
        {
            Coords target = MoveCalculator.getFloodPathCoords(map, agentInfo, axeLocation.get(0));
            if (target != null)
                return target;
        }


        //Next we repeat the floodfill to any area with equal dynamite and the longest path
        List<Coords> dynamiteLocations = map.getDynamiteLocations();
        if (!dynamiteLocations.isEmpty())
        {
            int best = 0;
            Coords bestTarget = null;
            for (Coords tile : dynamiteLocations)
            {
                FloodLengthReturn path = MoveCalculator.getLongestFloodPathToCoords(map, agentInfo, tile);
                if (path != null)
                {
                    if(path.getHighestLevel() > best)
                    {
                        best = path.getHighestLevel();
                        bestTarget = path.getTarget();
                    }
                }
            }
            if (bestTarget != null)
                return bestTarget;
        }

        //if we have the axe/cant see the axe, have maximised dynamite, and still cant get to/see the gold, then we will
        //accept losing dynamite to get to an area that is not flooded
        List<Coords> unFloodedLocations = map.getUnfloodedLocations();
        if (!unFloodedLocations.isEmpty())
        {
            for (Coords tile : unFloodedLocations)
            {
                Coords target = MoveCalculator.getFloodPathCoords(map, agentInfo, tile);
                if (target != null)
                    return target;
            }
        }

        return null; //-- nothing more to do here, the map is unsolvable except by luck
    }
}
