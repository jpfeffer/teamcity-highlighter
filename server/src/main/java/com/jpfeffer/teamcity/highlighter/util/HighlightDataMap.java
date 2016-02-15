package com.jpfeffer.teamcity.highlighter.util;

import com.jpfeffer.teamcity.highlighter.domain.HighlightData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>Decorator for processing {@link com.jpfeffer.teamcity.highlighter.domain.HighlightData} instances for the same key
 * (title).</p>
 *
 * @author jpfeffer
 * @since 2/12/2016
 */
public class HighlightDataMap implements Map<String, HighlightData>
{
    private final Map<String, HighlightData> map;

    public HighlightDataMap()
    {
        this(0);
    }

    public HighlightDataMap(int initialCapacity)
    {
        map = new HashMap<>(initialCapacity);
    }

    @Override
    public int size()
    {
        return map.size();
    }

    @Override
    public boolean isEmpty()
    {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key)
    {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value)
    {
        return map.containsValue(value);
    }

    @Override
    public HighlightData get(Object key)
    {
        return map.get(key);
    }

    @Override
    public HighlightData put(String key, HighlightData data)
    {
        if (key == null)
        {
            throw new IllegalArgumentException(String.format("Cannot put '%s' data when the key is null!", data));
        }
        if (data == null)
        {
            throw new IllegalArgumentException("Cannot put null data!");
        }

        if (map.containsKey(key))
        {
            final List<String> values = new ArrayList<>(map.get(key).getValues());
            values.addAll(data.getValues());
            return map.put(key, new HighlightData(key, values, data.getLevel(), data.getBlock(), data.getOrder()));
        }
        return map.put(key, data);
    }

    @Override
    public HighlightData remove(Object key)
    {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends HighlightData> m)
    {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public void clear()
    {
        map.clear();
    }

    @NotNull
    @Override
    public Set<String> keySet()
    {
        return map.keySet();
    }

    @NotNull
    @Override
    public Collection<HighlightData> values()
    {
        return map.values();
    }

    @NotNull
    @Override
    public Set<Entry<String, HighlightData>> entrySet()
    {
        return map.entrySet();
    }
}
