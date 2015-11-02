public class AgentInfoImpl implements AgentInfo
{
    Coords coords;
    Coords origin;
    char direction;
    boolean axe;
    boolean boat;
    boolean gold;
    int dynamite;

    AgentInfoImpl(Coords coords, char direction)
    {
        this.coords = coords;
        this.origin = new Coords(coords.getX(), coords.getY());
        this.direction = direction;
        axe = false;
        boat = false;
        gold = false;
        dynamite = 0;
    }

    @Override
    public void turnLeft()
    {
        if (direction == 'u')
            direction = 'l';
        else if (direction == 'l')
            direction = 'd';
        else if (direction == 'd')
            direction = 'r';
        else if (direction == 'r')
            direction = 'u';
    }

    @Override
    public void turnRight()
    {
        if (direction == 'u')
            direction = 'r';
        else if (direction == 'l')
            direction = 'u';
        else if (direction == 'd')
            direction = 'l';
        else if (direction == 'r')
            direction = 'd';
    }

    @Override
    public void moveForwards()
    {
        if (direction == 'u')
        {
            coords.setX(coords.getX() - 1);
        }
        else if (direction == 'l')
        {
            coords.setY(coords.getY() - 1);
        }
        else if (direction == 'd')
        {
            coords.setX(coords.getX() + 1);
        }
        else if (direction == 'r')
        {
            coords.setY(coords.getY() + 1);
        }
    }

    @Override
    public char getDirection()
    {
        return direction;
    }

    @Override
    public Coords getCoords()
    {
        return coords;
    }

    @Override
    public Coords getCoordsInFront()
    {
        Coords c = null;
        if (direction == 'u')
            c = coords.up();
        else if (direction == 'l')
            c = coords.left();
        else if (direction == 'd')
            c = coords.down();
        else if (direction == 'r')
            c = coords.right();
        return c;
    }

    @Override
    public char getDirectionChar()
    {
        char c = '^';
        if (direction == 'u')
            c = '^';
        else if (direction == 'l')
            c = '<';
        else if (direction == 'd')
            c = 'v';
        else if (direction == 'r')
            c = '>';
        return c;
    }

    @Override
    public boolean hasAxe()
    {
        return axe;
    }

    @Override
    public void setAxe(boolean hasAxe)
    {
        this.axe = hasAxe;
    }

    @Override
    public boolean inBoat()
    {
        return boat;
    }

    @Override
    public int dynamiteHeld()
    {
        return dynamite;
    }

    @Override
    public void useDynamite()
    {
        dynamite--;
    }

    @Override
    public void addDynamite()
    {
        dynamite++;
    }

    @Override
    public void setInBoat(boolean inBoat)
    {
        this.boat = inBoat;
    }

    @Override
    public Coords getOrigin()
    {
        return origin;
    }

    @Override
    public boolean hasGold()
    {
        return gold;
    }

    @Override
    public void setGold(boolean gold)
    {
        this.gold = gold;
    }
}
