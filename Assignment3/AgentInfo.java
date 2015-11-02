//Holds all relevant information on the agent
public interface AgentInfo
{
    //turn agent left
    void turnLeft();

    //turn agent right
    void turnRight();

    //move agent forwards
    void moveForwards();

    //get direction faced ('u','d','l','r')
    char getDirection();

    //Get coordinates of agent
    Coords getCoords();

    //Get coords in front of agent
    Coords getCoordsInFront();

    //get char to represent agent and direction ('^','v','<','>')
    char getDirectionChar();

    //whether or not axe is in possession
    boolean hasAxe();

    //sets whether agent has an axe
    void setAxe(boolean setAxe);

    //Whether in boat or not
    boolean inBoat();

    //number of sticks of dynamite held
    int dynamiteHeld();

    //decrement number of dynamite sticks
    void useDynamite();

    //increment number of dynamite held
    void addDynamite();

    //Set whether in boat or not
    void setInBoat(boolean inBoat);

    //Gets the starting square
    Coords getOrigin();

    //returns true if gold has been picked up
    boolean hasGold();

    //Sets whether the gold has been picked up
    void setGold(boolean gold);
}
