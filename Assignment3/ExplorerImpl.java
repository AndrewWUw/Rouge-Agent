import java.util.*;

public class ExplorerImpl implements Explorer
{
    HashMap<Integer, Boolean> visited;
    Stack<Coords> squaresToVisit;
    Queue<Character> moves;

    ExplorerImpl(Coords startingPosition, Queue<Character> moveList)
    {
        visited = new HashMap<Integer, Boolean>();
        squaresToVisit = new Stack<Coords>();
        moves = moveList;
    }

    public void addExplore(AgentInfo agentInfo, GameMap map)
    {
        if (visited.get(agentInfo.getCoords().hashCode()) != null)
        {
            return;
        }
        //add current square to visited
        visited.put(agentInfo.getCoords().hashCode(), true);
        List<Coords> toRemove1 = new ArrayList<Coords>();
        for (Coords c : squaresToVisit)
        {
            if (agentInfo.getCoords().equals(c))
                toRemove1.add(c);
        }
        squaresToVisit.removeAll(toRemove1);

        //get l/r/u/d of current position
        //add each to top of squares to visit stack if not visited and is visitable, if already in 'squares to visit, delete.
        List<Coords> neighbours = new ArrayList<Coords>();
        neighbours.add(agentInfo.getCoords().left());
        neighbours.add(agentInfo.getCoords().up());
        neighbours.add(agentInfo.getCoords().right());
        neighbours.add(agentInfo.getCoords().down());
        //for all neighbours
        for (Coords coords : neighbours)
        {
            //must be valid square to move into
            if (MoveCalculator.validMoveSquare(map, coords, agentInfo.inBoat(), agentInfo.hasAxe(), false))
            {
                //test that we havent already been there
                if (visited.get(coords.hashCode()) == null)
                {
                    //if we already wanted to visit it but are now closer, replace
                    List<Coords> toRemove2 = new ArrayList<Coords>();
                    for (Coords c : squaresToVisit)
                    {
                        if (coords.equals(c))
                            toRemove2.add(c);
                    }
                    squaresToVisit.removeAll(toRemove2);
                    squaresToVisit.push(coords);
                }
            }
        }
    }

    @Override
    public Coords getNext()
    {
        //get next from squares to visit (most recent first)
        return squaresToVisit.pop();
    }

    @Override
    public boolean hasNext()
    {
        return !squaresToVisit.isEmpty();
    }
}
