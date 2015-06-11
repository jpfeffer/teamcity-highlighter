package com.jpfeffer.teamcity.highlighter.service;

import com.jpfeffer.teamcity.highlighter.domain.Block;
import com.jpfeffer.teamcity.highlighter.domain.HighlightDataMap;
import com.jpfeffer.teamcity.highlighter.domain.Level;
import com.jpfeffer.teamcity.highlighter.message.HighlightMessageKey;
import com.jpfeffer.teamcity.highlighter.util.Util;
import jetbrains.buildServer.serverSide.CustomDataStorage;
import jetbrains.buildServer.serverSide.SBuild;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * <p>{@link DataStoreService} that implements saving/loading of custom data in {@link Map} format using TeamCity's
 * {@link CustomDataStorage} that stores the data on file system. Every data storage is associated with a particular
 * {@link jetbrains.buildServer.serverSide.SBuildType}.</p>
 *
 * <p>{@link CustomDataStorage} identifies data by given id that is constructed by concatenating {@link
 * TeamCityCustomDataStoreService#STORAGE_PREFIX} and currently running {@link SBuild} instance.</p>
 *
 * <p>Message data don't get overridden for the same {@link com.jpfeffer.teamcity.highlighter.message.HighlightMessageKey#title}
 * but their text will get grouped together.</p>
 *
 * @author jpfeffer
 * @since 1/26/15
 */
public class TeamCityCustomDataStoreService implements DataStoreService<Map<String, String>>
{
    private static final Logger LOG = Logger.getLogger(TeamCityCustomDataStoreService.class.getName());

    private static final String STORAGE_PREFIX = "hlght_ds_";

    @Override
    public void saveData(@NotNull SBuild sBuild, Map<String, String> data)
    {
        if (sBuild == null || sBuild.getBuildType() == null)
        {
            LOG.warning("Couldn't save message as build or associated build type was null.");
            return;
        }
        if (data == null || data.isEmpty())
        {
            LOG.warning("Couldn't save message as it was empty.");
            return;
        }
        if (data.get(HighlightMessageKey.title.name()) == null)
        {
            LOG.warning("Couldn't save message as the message title was not given");
            return;
        }

        final CustomDataStorage customDataStorage = sBuild.getBuildType().getCustomDataStorage(STORAGE_PREFIX + sBuild.getBuildId());
        final String levelParam = data.get(HighlightMessageKey.level.name());
        final String blockParam = data.get(HighlightMessageKey.block.name());
        final HighlightDataMap highlightDataMap = new HighlightDataMap(loadData(sBuild),
                                                                       Util.valueOfOrNull(levelParam, Level.class),
                                                                       Util.valueOfOrNull(blockParam, Block.class));
        highlightDataMap.put(data.get(HighlightMessageKey.title.name()), data.get(HighlightMessageKey.text.name()));

        final Set<String> keys = highlightDataMap.keySet();
        for (String key : keys)
        {
            customDataStorage.putValue(key, highlightDataMap.get(key));
        }
        customDataStorage.flush();
    }

    @Override
    public Map<String, String> loadData(@NotNull SBuild sBuild)
    {
        if (sBuild == null || sBuild.getBuildType() == null)
        {
            LOG.warning(String.format("Couldn't load data for build %s", sBuild));
            return new HashMap<>(0);
        }

        final Map<String, String> data = sBuild.getBuildType().getCustomDataStorage(STORAGE_PREFIX + sBuild.getBuildId()).getValues();
        return data == null ? new HashMap<String, String>(0) : data;
    }
}
