package com.jpfeffer.teamcity.highlighter.extension;

import com.google.common.collect.ImmutableMap;
import com.jpfeffer.teamcity.highlighter.domain.Level;
import com.jpfeffer.teamcity.highlighter.model.HighlightDataModel;
import com.jpfeffer.teamcity.highlighter.service.DataStoreService;
import jetbrains.buildServer.serverSide.SBuild;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.web.openapi.PagePlaces;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

/**
 * <p>Tests functionality of {@link HighlighterPageExtension} class.</p>
 *
 * @author jpfeffer
 * @since 1/30/15
 */
@RunWith(MockitoJUnitRunner.class)
public class HighlighterPageExtensionTest
{
    private HighlighterPageExtension highlighterPageExtension;

    private static final Map<String, String> HIGHLIGHT_TEST_MAP = new HashMap<>();
    static
    {
        HIGHLIGHT_TEST_MAP.put("key1", "val1");
        HIGHLIGHT_TEST_MAP.put("key2::error", "val2::val3");
    }

    @Mock
    private PagePlaces pagePlaces;
    @Mock
    private SBuild sBuild;
    @Mock
    private SBuildServer sBuildServer;
    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private DataStoreService dataStoreService;
    @Mock
    private HighlightDataModel model;

    @Before
    public void setUp() throws Exception
    {
        highlighterPageExtension = new HighlighterPageExtension(pagePlaces, sBuildServer);
        setField(highlighterPageExtension, "dataStoreService", dataStoreService);
        when(httpServletRequest.getParameter(eq("buildId"))).thenReturn("1234");
        when(sBuildServer.findBuildInstanceById(eq(Long.valueOf("1234")))).thenReturn(sBuild);
        when(dataStoreService.loadData(eq(sBuild))).thenReturn(HIGHLIGHT_TEST_MAP);
    }

    @Test
    public void testFillModel() throws Exception
    {
        final HashMap<String, Object> map = new HashMap<>();
        highlighterPageExtension.isAvailable(httpServletRequest);
        highlighterPageExtension.fillModel(map, httpServletRequest);

        assertThat((String) map.get("pluginId"), is("highlighter-plugin"));
        final HighlightDataModel dataModel = (HighlightDataModel) map.get("model");
        assertThat(dataModel.getHighlightData().size(), is(2));
        assertThat(dataModel.getHighlightData().get(0).getCollapsedByDefault(), is(false));
        assertThat(dataModel.getHighlightData().get(0).getLevel(), is(Level.error)); //Key with error level is expected to be first in the list after sort
        assertThat(dataModel.getHighlightData().get(0).getValues().size(), is(2));
        assertThat(dataModel.getHighlightData().get(1).getLevel(), is(Level.info));
        assertThat(dataModel.getHighlightData().get(1).getValues().size(), is(1));
    }

    @Test
    public void testIsAvailable() throws Exception
    {
        when(dataStoreService.loadData(eq(sBuild))).thenReturn(ImmutableMap.of("Intermittent Tests::warn", "testA::testB"));
        final boolean available = highlighterPageExtension.isAvailable(httpServletRequest);

        verify(sBuildServer).findBuildInstanceById(1234);
        verify(dataStoreService).loadData(eq(sBuild));
        assertThat(available, is(true));
    }

    @Test
    public void testIsAvailable_not_available() throws Exception
    {
        when(dataStoreService.loadData(any(SBuild.class))).thenReturn(new HashMap<>(0));
        final boolean available = highlighterPageExtension.isAvailable(httpServletRequest);

        verify(sBuildServer).findBuildInstanceById(1234);
        verify(dataStoreService).loadData(eq(sBuild));
        assertThat(available, is(false));
    }

    @Test
    public void testIsAvailable_not_available_nfe() throws Exception
    {
        when(httpServletRequest.getParameter(eq("buildId"))).thenReturn("abc");
        final boolean available = highlighterPageExtension.isAvailable(httpServletRequest);

        verify(sBuildServer, never()).findBuildInstanceById(anyLong());
        verify(dataStoreService, never()).loadData(any(SBuild.class));
        assertThat(available, is(false));
    }
}
