public class FloodLengthReturn
{
    Coords target;
    int highestLevel;

    public FloodLengthReturn(Coords target, int highestLevel)
    {
        this.target = target;
        this.highestLevel = highestLevel;
    }

    public Coords getTarget()
    {
        return target;
    }

    public int getHighestLevel()
    {
        return highestLevel;
    }
}
