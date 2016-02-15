package com.jpfeffer.teamcity.highlighter.service;

import jetbrains.buildServer.serverSide.SBuild;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * <p>Interface defining operations on data.</p>
 *
 * @author jpfeffer
 * @since 1/26/15
 */
public interface DataStoreService<T>
{
    /**
     * Saves given {@link T} data for the given {@link SBuild} instance.
     *
     * @param sBuild {@link SBuild} representing currently running build to save the data for.
     * @param data {@link T} data to be saved.
     */
    void saveData(@NotNull SBuild sBuild, T data);

    /**
     * Loads {@link T} data for the given {@link SBuild} instance.
     *
     * @param sBuild {@link SBuild} representing currently running build to get the data for.
     * @return {@link java.util.Collection} of {@link T} data loaded for the build.
     */
    Collection<T> loadData(@NotNull SBuild sBuild);
}
