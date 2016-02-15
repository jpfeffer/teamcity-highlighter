package com.jpfeffer.teamcity.highlighter.service;

import com.jpfeffer.teamcity.highlighter.domain.Block;
import com.jpfeffer.teamcity.highlighter.domain.HighlightData;
import com.jpfeffer.teamcity.highlighter.domain.Level;
import com.jpfeffer.teamcity.highlighter.domain.Order;
import com.jpfeffer.teamcity.highlighter.util.CustomStorageMap;
import jetbrains.buildServer.serverSide.CustomDataStorage;
import jetbrains.buildServer.serverSide.SBuild;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import static com.jpfeffer.teamcity.highlighter.util.CustomStorageMap.*;
import static com.jpfeffer.teamcity.highlighter.util.Util.valueOfOrDefault;

/**
 * <p>{@link DataStoreService} that implements saving/loading of custom data in {@link Map} format using TeamCity's
 * {@link CustomDataStorage} that stores the data on file system. Every data storage is associated with a particular
 * {@link jetbrains.buildServer.serverSide.SBuildType}.</p>
 *
 * <p>{@link CustomDataStorage} identifies data by given id that is constructed by concatenating {@link
 * TeamCityCustomDataStoreService#STORAGE_PREFIX} and currently running {@link SBuild} instance.</p>
 *
 * <p>Message data don't get overridden for the same {@link com.jpfeffer.teamcity.highlighter.message.HighlightMessageKey#title}
 * but their text will be grouped together.</p>
 *
 * <p>Copyright © 2000-2009, jpfeffer, Inc.</p>
 *
 * @author jpfeffer
 * @version 2012.2
 * @since 1/26/15
 */
public class TeamCityCustomDataStoreService implements DataStoreService<HighlightData>
{
    private static final Logger LOG = Logger.getLogger(TeamCityCustomDataStoreService.class.getName());

    private static final String STORAGE_PREFIX = "hlght_ds_";

    @Override
    public void saveData(@NotNull SBuild sBuild, HighlightData data)
    {
        if (sBuild == null || sBuild.getBuildType() == null)
        {
            LOG.warning("Couldn't save data as build or associated build type was null.");
            return;
        }
        if (data == null)
        {
            LOG.warning("Couldn't save data as it was empty.");
            return;
        }
        if (data.getKey() == null)
        {
            LOG.warning("Couldn't save data as the message title was not given");
            return;
        }

        final CustomDataStorage customDataStorage = sBuild.getBuildType().getCustomDataStorage(STORAGE_PREFIX + sBuild.getBuildId());
        final CustomStorageMap customStorageMap = new CustomStorageMap(loadDataInternal(sBuild),
                                                                       data.getLevel(),
                                                                       data.getBlock(),
                                                                       data.getOrder());
        customStorageMap.put(data.getKey(), data.getSingleValue());

        final Set<String> keys = customStorageMap.keySet();
        for (String key : keys)
        {
            customDataStorage.putValue(key, customStorageMap.get(key));
        }
        customDataStorage.flush();
    }

    @Override
    public Collection<HighlightData> loadData(@NotNull SBuild sBuild)
    {
        final List<HighlightData> ret = new ArrayList<>(0);
        final CustomStorageMap customStorageMap = new CustomStorageMap(loadDataInternal(sBuild));
        for (final String key : customStorageMap.keySet())
        {
            final String[] keyData = parseData(key);
            ret.add(new HighlightData(keyData[KEY_INDEX], customStorageMap.getAsList(key),
                                      valueOfOrDefault(keyData[LEVEL_INDEX], Level.class, Level.info),
                                      valueOfOrDefault(keyData[BLOCK_INDEX], Block.class, Block.expanded),
                                      valueOfOrDefault(keyData[ORDER_INDEX], Order.class, Order.none)));

        }
        return ret;
    }

    private Map<String, String> loadDataInternal(@NotNull SBuild sBuild)
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
