import java.util.ArrayList;
import java.util.List;

public class MapImpl implements GameMap
{
    //Private variables
    private char[][] grid;
    private int[][] flood;
    private AgentInfo agent;
    private int mapSize;

    MapImpl (char[][] view, AgentInfo agentInfo)
    {
        mapSize = 164;
        grid = new char[mapSize][mapSize];
        flood = new int[mapSize][mapSize];
        for (int i = 0; i < mapSize; i++)
        {
            for (int j = 0; j < mapSize; j++)
            {
                grid[i][j] = 'x';
                flood[i][j] = 0;
            }
        }
        agent = agentInfo;
        for (int x = 0; x < 5; x++)
        {
            System.arraycopy(view[x], 0, grid[x + ((mapSize/2) - 2)], ((mapSize/2) - 2), 5);
        }
        flood[agent.getCoords().getX()][agent.getCoords().getY()] = 1;
    }

    //Modify the map and agent based on the move submitted
    @Override
    public void update(char action, char[][] view)
    {
        grid[agent.getCoords().getX()][agent.getCoords().getY()] = ' ';
        switch(action)
        {
            case 'l': case 'L':
                agent.turnLeft();
                break;
            case 'r': case 'R':
                agent.turnRight();
                break;
            case 'f': case 'F':
                if (grid[agent.getCoordsInFront().getX()][agent.getCoordsInFront().getY()] == 'a')
                    agent.setAxe(true);
                if (grid[agent.getCoordsInFront().getX()][agent.getCoordsInFront().getY()] == 'd')
                    agent.addDynamite();
                if (grid[agent.getCoordsInFront().getX()][agent.getCoordsInFront().getY()] == 'B')
                    agent.setInBoat(true);
                if (grid[agent.getCoordsInFront().getX()][agent.getCoordsInFront().getY()] == 'g')
                    agent.setGold(true);
                if (agent.inBoat() && grid[agent.getCoordsInFront().getX()][agent.getCoordsInFront().getY()] == ' ')
                    agent.setInBoat(false);
                agent.moveForwards();
                addLine(agent, view);
                break;
            case 'b': case 'B':
                agent.useDynamite();
            case 'c': case 'C':
                removeSquareInFront(agent);
                break;
            default:
                break;
        }
        grid[agent.getCoords().getX()][agent.getCoords().getY()] = agent.getDirectionChar();
        flood[agent.getCoords().getX()][agent.getCoords().getY()] = 1;
    }

    //When chop/blast, make the square into a land tile = ' '
    private void removeSquareInFront(AgentInfo agent)
    {
        switch (agent.getDirection())
        {
            case 'u':
                grid[agent.getCoords().getX() - 1][agent.getCoords().getY()] = ' ';
                break;
            case 'd':
                grid[agent.getCoords().getX() + 1][agent.getCoords().getY()] = ' ';
                break;
            case 'l':
                grid[agent.getCoords().getX()][agent.getCoords().getY() - 1] = ' ';
                break;
            case 'r':
                grid[agent.getCoords().getX()][agent.getCoords().getY() + 1] = ' ';
                break;
            default:
                break;
        }
    }

    @Override
    //Print the map in memory to screen
    public void printMap()
    {
        int i,j;

        System.out.println("\n+--------------------------------------------------+");
        for( i=0; i < mapSize; i++ ) {
            System.out.print("|");
            for( j=0; j < mapSize; j++ ) {
                System.out.print(grid[i][j]);
            }
            System.out.println("|");
        }
        System.out.println("+--------------------------------------------------+");
    }

    @Override
    public char getSquare(Coords coords)
    {
        return  grid[coords.getX()][coords.getY()];
    }

    @Override
    public boolean isWall(Coords coords)
    {
        return (grid[coords.getX()][coords.getY()] == '*');
    }

    @Override
    public boolean isLand(Coords coords)
    {
        return (grid[coords.getX()][coords.getY()] == ' ');
    }

    @Override
    public boolean isSea(Coords coords)
    {
        return (grid[coords.getX()][coords.getY()] == '~');
    }

    @Override
    public boolean isDynamite(Coords coords)
    {
        return (grid[coords.getX()][coords.getY()] == 'd');
    }

    @Override
    public boolean isTree(Coords coords)
    {
        return (grid[coords.getX()][coords.getY()] == 'T');
    }

    @Override
    public boolean isBoat(Coords coords)
    {
        return (grid[coords.getX()][coords.getY()] == 'B');
    }

    @Override
    public boolean isGold(Coords coords)
    {
        return (grid[coords.getX()][coords.getY()] == 'g');
    }

    @Override
    public boolean isAxe(Coords coords)
    {
        return (grid[coords.getX()][coords.getY()] == 'a');
    }

    //returns locations of all visible dynamite
    @Override
    public List<Coords> getDynamiteLocations()
    {
        return findItem('d');
    }

    //returns location of a tree
    @Override
    public List<Coords> getTreeLocations()
    {
        return findItem('T');
    }

    //returns location of gold
    @Override
    public List<Coords> getGoldLocations()
    {
        return findItem('g');
    }

    @Override
    public int[][] getFloodMap()
    {
        return flood;
    }

    @Override
    public List<Coords> getAxeLocations()
    {
        return findItem('a');
    }

    @Override
    public List<Coords> getUnfloodedLocations()
    {
        List<Coords> items = new ArrayList<Coords>();
        for (int i = 0; i < mapSize; i++)
        {
            for (int j = 0; j < mapSize; j++)
            {
                Coords current = new Coords(i, j);
                if (grid[i][j] == ' ' && flood[i][j] == 0)
                {
                    items.add(current);
                }
            }
        }
        return items;
    }

    @Override
    public int[][] copyMap(int[][] map)
    {
        int[][] newFlood = new int[mapSize][mapSize];

        //explicit copy so array is not a reference to previous array
        int i,j;
        for( i=0; i < mapSize; i++ ) {
            for( j=0; j < mapSize; j++ ) {
                newFlood[i][j] = map[i][j];
            }
        }
        return newFlood;
    }

    public List<Coords> adjacentToFillLevelList(int[][] flood, int level)
    {
        List<Coords> adjacent = new ArrayList<Coords>();
        for (int i = 0; i < mapSize; i++)
        {
            for (int j = 0; j < mapSize; j++)
            {
                if(flood[i][j] == 0 && grid[i][j] == '*')
                {
                    boolean adj = false;
                    if (flood[i+1][j] == level)
                        adj = true;
                    else if (flood[i-1][j] == level)
                        adj = true;
                    else if (flood[i][j+1] == level)
                        adj = true;
                    else if (flood[i][j-1] == level)
                        adj = true;
                    if (adj)
                    {
                        adjacent.add(new Coords(i, j));
                    }
                }
            }
        }
        return adjacent;
    }

    private List<Coords> findItem(char c)
    {
        List<Coords> items = new ArrayList<Coords>();
        for (int i = 0; i < mapSize; i++)
        {
            for (int j = 0; j < mapSize; j++)
            {
                Coords current = new Coords(i, j);
                if (grid[i][j] == c)
                {
                    items.add(current);
                }
            }
        }
        return items;
    }

    //Adds a new line to the map in memory from the new 'view'
    private void addLine(AgentInfo agent, char[][] view)
    {
        //fill in the newly revealed row of information, based on the direction you are facing
        switch (agent.getDirection())
        {
            case 'u':
                for (int i = 0; i < 5; i++)
                {
                    grid[agent.getCoords().getX() - 2][agent.getCoords().getY() - 2 + i] = view[0][i];
                }
                //Fill in the square you just left as well
                grid[agent.getCoords().getX() + 1][agent.getCoords().getY()] = view[3][2];
                break;
            case 'd':
                for (int i = 0; i < 5; i++)
                {
                    grid[agent.getCoords().getX() + 2][agent.getCoords().getY() + 2 - i] = view[0][i];
                }
                //Fill in the square you just left as well
                grid[agent.getCoords().getX() - 1][agent.getCoords().getY()] = view[3][2];
                break;
            case 'l':
                for (int i = 0; i < 5; i++)
                {
                    grid[agent.getCoords().getX() + 2 - i][agent.getCoords().getY() - 2] = view[0][i];
                }
                //Fill in the square you just left as well
                grid[agent.getCoords().getX()][agent.getCoords().getY() + 1] = view[3][2];
                break;
            case 'r':
                for (int i = 0; i < 5; i++)
                {
                    grid[agent.getCoords().getX() - 2 + i][agent.getCoords().getY() + 2] = view[0][i];
                }
                //Fill in the square you just left as well
                grid[agent.getCoords().getX()][agent.getCoords().getY() - 1] = view[3][2];
                break;
            default:
                break;
        }
    }
}
