package com.jpfeffer.teamcity.highlighter.domain;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * <p>Domain object wrapping highlight data sent to TeamCity using custom message.</p>
 *
 * @author jpfeffer
 * @since 1/28/15
 */
public class HighlightData
{
    private final String key;
    private final List<String> values;
    private final Level level;
    private final Block block;
    private final Order order;

    public HighlightData(String key, List<String> values, Level level, Block block, Order order)
    {
        this.key = key;
        this.values = values;
        this.level = level;
        this.block = block;
        this.order = order;

        if (Order.alphabet.equals(order))
        {
            Collections.sort(this.values, new Comparator<String>()
            {
                @Override
                public int compare(String o1, String o2)
                {
                    return o1.compareTo(o2);
                }
            });
        }
    }

    public String getKey()
    {
        return key;
    }

    public List<String> getValues()
    {
        return values;
    }

    public String getSingleValue()
    {
        return values != null && values.size() == 1 ? values.get(0) : "";
    }

    public Level getLevel()
    {
        return level;
    }

    public Block getBlock()
    {
        return block;
    }

    public Order getOrder()
    {
        return order;
    }

    public boolean getCollapsedByDefault()
    {
        return Block.collapsed.equals(block);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HighlightData that = (HighlightData) o;

        if (block != that.block) return false;
        if (!key.equals(that.key)) return false;
        if (level != that.level) return false;
        if (order != that.order) return false;
        if (values != null ? !values.equals(that.values) : that.values != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = key.hashCode();
        result = 31 * result + (values != null ? values.hashCode() : 0);
        result = 31 * result + level.hashCode();
        result = 31 * result + block.hashCode();
        result = 31 * result + order.hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        return "HighlightData{" +
               "key='" + key + '\'' +
               ", values=" + values +
               ", level=" + level +
               ", block=" + block +
               ", order=" + order +
               '}';
    }
}
