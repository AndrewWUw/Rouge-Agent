package utils;

import java.util.List;

public class DynamiteFloodReturn
{
    List<Coords> path;
    int highestLevel;

    public DynamiteFloodReturn(List<Coords> path, int highestLevel)
    {
        this.path = path;
        this.highestLevel = highestLevel;
    }

    public List<Coords> getPath()
    {
        return path;
    }

    public int getHighestLevel()
    {
        return highestLevel;
    }
}
