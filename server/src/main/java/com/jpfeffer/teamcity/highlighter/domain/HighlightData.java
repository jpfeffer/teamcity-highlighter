package com.jpfeffer.teamcity.highlighter.domain;

import java.util.Collection;

/**
 * <p>Domain object wrapping highlight data sent to TeamCity using custom message.</p>
 *
 * @author jpfeffer
 * @since 1/28/15
 */
public class HighlightData
{
    private final String key;
    private final Collection<String> values;
    private final Level level;
    private final Block block;

    public HighlightData(String key, Collection<String> values, Level level, Block block)
    {
        this.key = key;
        this.values = values;
        this.level = level;
        this.block = block;
    }

    public String getKey()
    {
        return key;
    }

    public Collection<String> getValues()
    {
        return values;
    }

    public Level getLevel()
    {
        return level;
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
               '}';
    }
}
