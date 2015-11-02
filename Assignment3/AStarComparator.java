import java.util.Comparator;

public class AStarComparator implements Comparator<AStarUnit>
{
    //Used to order an Astar list based on the priority of least dynamite used and then shortest distance
    @Override
    public int compare(AStarUnit o1, AStarUnit o2)
    {
        if(o1.getDynamiteUsed() < o2.getDynamiteUsed())
        {
            return -1000;
        }
        else if (o1.getDynamiteUsed() == o2.getDynamiteUsed())
        {
            if(o1.getDynamite() > o2.getDynamite())
            {
                return -1000;
            }
            else if (o1.getDynamite() == o2.getDynamite())
            {
                return o1.getfScore() - o2.getfScore();
            }
        }
        return 1000;
    }
}
