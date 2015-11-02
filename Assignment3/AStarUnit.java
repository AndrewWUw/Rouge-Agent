public class AStarUnit
{
    private int gScore;
    private int hScore;
    private int fScore;
    private Coords coords;
    private AStarUnit parent;
    private int dynamite;
    private int dynamiteUsed;
    private boolean inBoat;

    public AStarUnit(int gScore, int hScore, int fScore, Coords coords, AStarUnit parent, int dynamite, int dynamiteUsed, boolean inBoat)
    {
        this.gScore = gScore;
        this.hScore = hScore;
        this.fScore = fScore;
        this.coords = coords;
        this.parent = parent;
        this.dynamite = dynamite;
        this.dynamiteUsed = dynamiteUsed;
        this.inBoat = inBoat;
    }

    public int gethScore()
    {
        return hScore;
    }

    public int getfScore()
    {
        return fScore;
    }

    public void setfScore(int fScore)
    {
        this.fScore = fScore;
    }

    public Coords getCoords()
    {
        return coords;
    }

    public int getgScore()
    {
        return gScore;
    }

    public void setgScore(int gScore)
    {
        this.gScore = gScore;
    }

    public AStarUnit getParent()
    {
        return parent;
    }

    public void setParent(AStarUnit parent)
    {
        this.parent = parent;
    }

    public int getDynamite()
    {
        return dynamite;
    }

    public void setDynamite(int dynamite)
    {
        this.dynamite = dynamite;
    }

    public int getDynamiteUsed()
    {
        return dynamiteUsed;
    }

    public void setDynamiteUsed(int dynamiteUsed)
    {
        this.dynamiteUsed = dynamiteUsed;
    }

    public boolean isInBoat()
    {
        return inBoat;
    }

    public void setInBoat(boolean inBoat)
    {
        this.inBoat = inBoat;
    }
}