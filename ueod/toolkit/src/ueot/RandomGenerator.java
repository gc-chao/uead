
package ueot;

import java.util.*;

public class RandomGenerator extends Random
{
    private static final long serialVersionUID = 1L;

    public RandomGenerator()
    {
        super();
    }

    public RandomGenerator(long seed)
    {
        super(seed);
    }

    public int nextRange(int to)
    {
        return nextInt(to);
    }

    public int nextRange(int from, int to)
    {
        return nextInt(to - from + 1) + from;
    }

    public <T> T getListItem(List<T> list)
    {
        return list.get(nextInt(list.size()));
    }
}