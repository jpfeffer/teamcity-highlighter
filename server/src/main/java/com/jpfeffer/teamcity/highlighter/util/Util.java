package com.jpfeffer.teamcity.highlighter.util;

/**
 * <p>Class containing utility methods.</p>
 *
 * @author jpfeffer
 * @since 3/11/2015
 */
public final class Util
{
    private Util() {}

    /**
     * Returns an {@link java.lang.Enum} instance of type {@link T} for the given name.
     *
     * @param name Name to get the enum instance for.
     * @param type {@link java.lang.Class} of type {@link T} to be the enum of.
     * @return {@link T} instance or null if is not of type for the name.
     */
    public static <T extends Enum<T>> T valueOfOrNull(String name, Class<T> type)
    {
        T ret;
        try
        {
            ret = Enum.valueOf(type, name);
        }
        catch (Exception e)
        {
            ret = null;
        }
        return ret;
    }

    /**
     * Returns an {@link java.lang.Enum} instance of type {@link T} for the given name.
     *
     * @param name Name to get the enum instance for.
     * @param type {@link java.lang.Class} of type {@link T} to be the enum of.
     * @param def {@link T} instance to be used as default.
     * @return {@link T} instance or the given {@link T} default if is not of type for the name.
     */
    public static <T extends Enum<T>> T valueOfOrDefault(String name, Class<T> type, T def)
    {
        T ret;
        try
        {
            ret = Enum.valueOf(type, name);
        }
        catch (Exception e)
        {
            ret = def;
        }
        return ret;
    }

    /**
     * Returns {@link java.lang.Enum#name} value of type {@link T} for the given name if exists.
     *
     * @param name Name to get the enum instance for.
     * @param type {@link java.lang.Class} of type {@link T} to be the enum of.
     * @return Name of the enum instance if found or null.
     */
    public static <T extends Enum<T>> String nameOfOrNull(String name, Class<T> type)
    {
        final T value = valueOfOrNull(name, type);
        return value != null ? value.name() : null;
    }


}
