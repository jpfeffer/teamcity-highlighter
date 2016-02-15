package com.jpfeffer.teamcity.highlighter.service;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.jpfeffer.teamcity.highlighter.domain.Block;
import com.jpfeffer.teamcity.highlighter.domain.HighlightData;
import com.jpfeffer.teamcity.highlighter.domain.Level;
import com.jpfeffer.teamcity.highlighter.domain.Order;
import com.jpfeffer.teamcity.highlighter.message.HighlightMessageKey;
import com.jpfeffer.teamcity.highlighter.util.HighlightDataMap;
import jetbrains.buildServer.serverSide.SBuild;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

import static com.jpfeffer.teamcity.highlighter.util.Util.valueOfOrDefault;
import static java.util.logging.Level.SEVERE;

/**
 * <p>{@link com.jpfeffer.teamcity.highlighter.service.DataStoreService} implementation based on MySQL DB that is used
 * as the backend storage for highlighter data.</p>
 *
 * @author jpfeffer
 * @since 2/10/2016
 */
public class MySQLDataStoreService implements DataStoreService<HighlightData>
{
    private static final Logger LOG = Logger.getLogger(MySQLDataStoreService.class.getName());

    @Resource(name = "highlighterDataSource")
    private ComboPooledDataSource dataSource;

    @Override
    public void saveData(@NotNull SBuild sBuild, HighlightData data)
    {
        if (sBuild == null)
        {
            LOG.warning("Couldn't save message as build was null.");
            return;
        }
        if (data == null)
        {
            LOG.warning("Couldn't save data as it was empty.");
            return;
        }
        if (data.getKey() == null)
        {
            LOG.warning("Couldn't save message as the message title was not given");
            return;
        }

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO hghlt_data VALUES (default, ?, ?, ?, ?, ?, ?, default)");
        )
        {
            statement.setString(1, String.valueOf(sBuild.getBuildId()));
            statement.setString(2, data.getKey());
            statement.setString(3, data.getSingleValue());
            statement.setString(4, data.getLevel().name());
            statement.setString(5, data.getBlock().name());
            statement.setString(6, data.getOrder().name());

            statement.executeUpdate();
        }
        catch (Exception e)
        {
            LOG.log(SEVERE, String.format("Saving of message with title '%s' for build with id '%s' failed", data.getKey(), sBuild.getBuildId()), e);
        }
    }

    @Override
    public Collection<HighlightData> loadData(@NotNull SBuild sBuild)
    {
        if (sBuild == null)
        {
            LOG.warning(String.format("Couldn't load data for build %s", sBuild));
            return new ArrayList<>(0);
        }

        final Map<String, HighlightData> ret = new HighlightDataMap();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(String.format("SELECT title, text, level, block, ordering FROM hghlt_data WHERE build_id = '%s'", sBuild.getBuildId()));
             ResultSet resultSet = statement.executeQuery()
        )
        {
            while (resultSet.next())
            {
                final String key = resultSet.getString(HighlightMessageKey.title.name());
                ret.put(key, new HighlightData(key,
                                               Arrays.asList(resultSet.getString(HighlightMessageKey.text.name())),
                                               valueOfOrDefault(resultSet.getString(HighlightMessageKey.level.name()), Level.class, Level.info),
                                               valueOfOrDefault(resultSet.getString(HighlightMessageKey.block.name()), Block.class, Block.collapsed),
                                               valueOfOrDefault(resultSet.getString("ordering"), Order.class, Order.none)));
            }
        }
        catch (Exception e)
        {
            LOG.log(SEVERE, String.format("Loading of data for build with id '%s' failed", sBuild.getBuildId()), e);
        }

        return ret.values();
    }
}
