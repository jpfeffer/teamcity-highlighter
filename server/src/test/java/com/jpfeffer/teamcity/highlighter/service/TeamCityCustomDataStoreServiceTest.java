package com.jpfeffer.teamcity.highlighter.service;

import com.jpfeffer.teamcity.highlighter.domain.Block;
import com.jpfeffer.teamcity.highlighter.domain.HighlightData;
import com.jpfeffer.teamcity.highlighter.domain.Level;
import jetbrains.buildServer.serverSide.CustomDataStorage;
import jetbrains.buildServer.serverSide.SBuild;
import jetbrains.buildServer.serverSide.SBuildType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * <p>Tests proper functionality of {@link TeamCityCustomDataStoreService} class.</p>
 *
 * @author jpfeffer
 * @since 2/2/15
 */
@RunWith(MockitoJUnitRunner.class)
public class TeamCityCustomDataStoreServiceTest
{
    private DataStoreService<HighlightData> dataStoreService;

    private static final Map<String, String> TEST_MAP = new HashMap<>();
    static
    {
        TEST_MAP.put("title", "Some title");
        TEST_MAP.put("text", "Some text");
        TEST_MAP.put("level", "error");
        TEST_MAP.put("block", "collapsed");
    }

    @Mock
    private SBuild sBuild;
    @Mock
    private SBuildType sBuildType;
    @Mock
    private CustomDataStorage customDataStorage;
    @Mock
    private HighlightData highlightData;

    @Before
    public void setUp() throws Exception
    {
        dataStoreService = new TeamCityCustomDataStoreService();
        when(sBuild.getBuildId()).thenReturn(1234L);
        when(sBuild.getBuildType()).thenReturn(sBuildType);
        when(sBuildType.getCustomDataStorage(eq("hlght_ds_1234"))).thenReturn(customDataStorage);
        when(customDataStorage.getValues()).thenReturn(TEST_MAP);
    }

    @Test
    public void testSaveData() throws Exception
    {
        when(highlightData.getKey()).thenReturn("Some title");
        when(highlightData.getSingleValue()).thenReturn("Some text");
        when(highlightData.getLevel()).thenReturn(Level.error);
        when(highlightData.getBlock()).thenReturn(Block.collapsed);
        dataStoreService.saveData(sBuild, highlightData);

        verify(customDataStorage).putValue(eq("text"), eq("Some text"));
        verify(customDataStorage).putValue(eq("title"), eq("Some title"));
        verify(customDataStorage).putValue(eq("level"), eq("error"));
        verify(customDataStorage).putValue(eq("Some title::error::collapsed"), eq("Some text"));
        verify(customDataStorage).flush();
    }

    @Test
    public void testSaveData_null_sbuild_type() throws Exception
    {
        when(sBuild.getBuildType()).thenReturn(null);

        dataStoreService.saveData(sBuild, highlightData);

        verify(customDataStorage, never()).putValue(anyString(), anyString());
        verify(customDataStorage, never()).flush();
    }

    @Test
    public void testSaveData_empty_data() throws Exception
    {
        dataStoreService.saveData(sBuild, highlightData);

        verify(customDataStorage, never()).putValue(anyString(), anyString());
        verify(customDataStorage, never()).flush();
    }

    @Test
    public void testSaveData_no_title_in_data() throws Exception
    {
        when(highlightData.getKey()).thenReturn(null);
        dataStoreService.saveData(sBuild, highlightData);

        verify(customDataStorage, never()).putValue(anyString(), anyString());
        verify(customDataStorage, never()).flush();
    }

    @Test
    public void testLoadData() throws Exception
    {
        final Collection<HighlightData> data = dataStoreService.loadData(sBuild);

        assertThat(data.size(), is(TEST_MAP.size()));
    }

    @Test
    public void testLoadData_null_sbuild_type() throws Exception
    {
        when(sBuild.getBuildType()).thenReturn(null);
        final Collection<HighlightData> data = dataStoreService.loadData(sBuild);

        assertThat(data.isEmpty(), is(true));
        verify(customDataStorage, never()).getValues();
    }
}
