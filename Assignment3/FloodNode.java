public class FloodNode
{
    private int floodLevel;
    private Coords goal;
    private int dynamite;
    private int[][] floodMap;
    private Coords floodStart;

    public FloodNode(int[][] floodMap, int floodLevel, Coords goal, int dynamite, Coords floodStart)
    {
        this.floodMap = floodMap;
        this.floodLevel = floodLevel;
        this.goal = goal;
        this.dynamite = dynamite;
        this.floodStart = floodStart;
    }

    public int[][] getFloodMap()
    {
        return floodMap;
    }

    public int getFloodLevel()
    {
        return floodLevel;
    }

    public Coords getGoal()
    {
        return goal;
    }

    public int getDynamite()
    {
        return dynamite;
    }

    public void setFloodMap(int[][] floodMap)
    {
        this.floodMap = floodMap;
    }

    public void setDynamite(int dynamite)
    {
        this.dynamite = dynamite;
    }

    public Coords getHighestFloodSquare()
    {
        int i,j;
        for( i=0; i < 164; i++ ) {
            for( j=0; j < 164; j++ ) {
                if (floodMap[i][j] == floodLevel)
                    return new Coords(i,j);
            }
        }
        return null;
    }

    public Coords getFloodStart()
    {
        return floodStart;
    }

    public void setFloodStart(Coords floodStart)
    {
        this.floodStart = floodStart;
    }
}
