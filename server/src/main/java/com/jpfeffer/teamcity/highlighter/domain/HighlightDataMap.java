package com.jpfeffer.teamcity.highlighter.domain;

import com.jpfeffer.teamcity.highlighter.util.Util;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import static java.util.logging.Level.FINE;

/**
 * <p>{@link Map} decorator that processes title-text pairs that may contain separators in form of {@link
 * HighlightDataMap#SEPARATOR}.</p>
 *
 * @author jpfeffer
 * @since 1/29/15
 */
public class HighlightDataMap implements Map<String, String>
{
    private static final Logger LOG = Logger.getLogger(HighlightDataMap.class.getName());

    private static final String SEPARATOR = "::";
    public static final byte KEY_INDEX = 0;
    public static final byte LEVEL_INDEX = 1;
    public static final byte BLOCK_INDEX = 2;

    private final Map<String, String> map;
    private Level level;
    private Block block;

    public HighlightDataMap(Map<String, String> map)
    {
        this.map = map;
    }

    public HighlightDataMap(Map<String, String> map, Level level, Block block)
    {
        this.map = map;
        this.level = level;
        this.block = block;
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
    public String get(Object key)
    {
        return map.get(key);
    }

    public Collection<String> getAsCollection(Object key)
    {
        return Arrays.asList(get(key).split(SEPARATOR));
    }

    @Override
    public String put(String key, String value)
    {
        if (key == null)
        {
            throw new IllegalArgumentException(String.format("Cannot put '%s' value when the key is null!", value));
        }
        if (key.contains(SEPARATOR))
        {
            throw new IllegalArgumentException(String.format("Couldn't save message data as the key '%s' contained reserved separator '::'", key));
        }

        String targetKey = key;
        if (level != null)
        {
            targetKey = targetKey.concat(SEPARATOR).concat(level.name());
        }
        if (block != null)
        {
            targetKey = targetKey.concat(SEPARATOR).concat(block.name());
        }

        if (map.containsKey(targetKey))
        {
            return map.put(targetKey, map.get(targetKey).concat(SEPARATOR).concat(value));
        }
        return map.put(targetKey, value);
    }

    @Override
    public String remove(Object key)
    {
        return map.remove(key);
    }

    @Override
    public void putAll(@NotNull Map<? extends String, ? extends String> m)
    {
        map.putAll(m);
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
    public Collection<String> values()
    {
        return map.values();
    }

    @NotNull
    @Override
    public Set<Entry<String, String>> entrySet()
    {
        return map.entrySet();
    }

    /**
     * Splits the given key in an array of {@link java.lang.String}s to get key, level, block values from it at their
     * indexes.
     *
     * @param key Key to get split.
     * @return String array from the key split by {@link HighlightDataMap#SEPARATOR}.
     * @see {@link java.lang.String#split(String regex)}
     */
    public static String[] getKeyData(String key)
    {
        if (!key.contains(SEPARATOR))
        {
            LOG.log(FINE, String.format("Could not split '%s' key, going to use defaults!", key));
            return new String[]{key, Level.info.name(), Block.expanded.name()};
        }

        final String[] keyData = key.split(SEPARATOR);
        String plainKey = null;
        String level = Level.info.name();
        String block = Block.expanded.name();
        for (int i = 0; i < keyData.length; i++)
        {
            if (i == 0)
            {
                plainKey = keyData[0];
            }
            else
            {
                if (Util.valueOfOrNull(keyData[i], Level.class) != null)
                {
                    level = Util.nameOfOrNull(keyData[i], Level.class);
                }
                else if (Util.valueOfOrNull(keyData[i], Block.class) != null)
                {
                    block = Util.nameOfOrNull(keyData[i], Block.class);
                }
            }
        }

        return new String[]{plainKey, level, block};
    }
}
