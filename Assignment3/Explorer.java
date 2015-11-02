//Used to step through a BFS exploration of all possible regions
public interface Explorer
{
    //Returns next move to make
    Coords getNext();

    //returns true if there are unvisited squares within reach
    boolean hasNext();

    //add details of current square to explorer info
    void addExplore(AgentInfo coords, GameMap gameMap);

}
