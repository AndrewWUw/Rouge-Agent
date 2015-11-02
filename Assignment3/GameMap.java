import java.util.List;

public interface GameMap
{

    //Update map based on action + new view
    void update(char action, char[][] view);

    //Print memory map to screen
    void printMap();

    //Get the character in a single square
    char getSquare(Coords coords);

    //returns true if the area at coords is a wall
    boolean isWall(Coords coords);

    //returns true if the area at coords is a land space
    boolean isLand(Coords coords);

    //returns true if the area at coords is in the ocean
    boolean isSea(Coords coords);

    //returns true if the area at coords is a dynamite
    boolean isDynamite(Coords coords);

    //returns true if the area at coords is a tree
    boolean isTree(Coords coords);

    //returns true if the area at coords is a boat
    boolean isBoat(Coords coords);

    //returns true if the area at coords is gold
    boolean isGold(Coords coords);

    //returns true if the area at coordinates is an axe
    boolean isAxe(Coords c);

    //return list of coordinates of dynamite
    List<Coords> getDynamiteLocations();

    //return list of coordinates of gold
    List<Coords> getGoldLocations();

    //return list of coordinates of trees
    List<Coords> getTreeLocations();

    //returns the flood map
    int[][] getFloodMap();

    //copies a given map
    int[][] copyMap(int[][] map);

    //returns all squares adjacent to a given level of fill on the given floodfill map
    List<Coords> adjacentToFillLevelList(int[][] flood, int level);

    //returns all/any locations of axes within view
    List<Coords> getAxeLocations();

    //returns all/any unexplored blank squares within view
    List<Coords> getUnfloodedLocations();
}
