package com.jpfeffer.teamcity.highlighter.service;

import jetbrains.buildServer.serverSide.CustomDataStorage;
import jetbrains.buildServer.serverSide.SBuild;
import jetbrains.buildServer.serverSide.SBuildType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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
    private DataStoreService<Map<String, String>> dataStoreService;

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
        dataStoreService.saveData(sBuild, TEST_MAP);

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

        dataStoreService.saveData(sBuild, new HashMap<String, String>(0));

        verify(customDataStorage, never()).putValue(anyString(), anyString());
        verify(customDataStorage, never()).flush();
    }

    @Test
    public void testSaveData_empty_data() throws Exception
    {
        dataStoreService.saveData(sBuild, new HashMap<String, String>(0));

        verify(customDataStorage, never()).putValue(anyString(), anyString());
        verify(customDataStorage, never()).flush();
    }

    @Test
    public void testSaveData_no_title_in_data() throws Exception
    {
        final HashMap<String, String> data = new HashMap<>(1);
        data.put("text", "Testing...");
        dataStoreService.saveData(sBuild, data);

        verify(customDataStorage, never()).putValue(anyString(), anyString());
        verify(customDataStorage, never()).flush();
    }

    @Test
    public void testLoadData() throws Exception
    {
        final Map<String, String> data = dataStoreService.loadData(sBuild);

        assertThat(data.equals(TEST_MAP), is(true));
    }

    @Test
    public void testLoadData_null_sbuild_type() throws Exception
    {
        when(sBuild.getBuildType()).thenReturn(null);
        final Map<String, String> data = dataStoreService.loadData(sBuild);

        assertThat(data.isEmpty(), is(true));
        verify(customDataStorage, never()).getValues();
    }
}
