package com.jpfeffer.teamcity.highlighter.message.translator;

import com.google.common.collect.ImmutableMap;
import com.jpfeffer.teamcity.highlighter.service.DataStoreService;
import jetbrains.buildServer.messages.BuildMessage1;
import jetbrains.buildServer.messages.serviceMessages.ServiceMessage;
import jetbrains.buildServer.serverSide.SRunningBuild;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

/**
 * <p>Tests proper functionality of {@link HighlightServiceMessageTranslator} class.</p>
 *
 * @author jpfeffer
 * @since 2/2/15
 */
@RunWith(MockitoJUnitRunner.class)
public class HighlightServiceMessageTranslatorTest
{
    private static final Map<String, String> TEST_MAP = ImmutableMap.of("key", "val");

    private HighlightServiceMessageTranslator highlightServiceMessageTranslator;

    @Mock
    private DataStoreService<Map<String, String>> dataStoreService;
    @Mock
    private SRunningBuild sRunningBuild;
    @Mock
    private BuildMessage1 buildMessage1;
    @Mock
    private ServiceMessage serviceMessage;

    @Before
    public void setUp() throws Exception
    {
        highlightServiceMessageTranslator = new HighlightServiceMessageTranslator();
        setField(highlightServiceMessageTranslator, "dataStoreService", dataStoreService);
    }

    @Test
    public void testTranslate() throws Exception
    {
        when(serviceMessage.getAttributes()).thenReturn(TEST_MAP);
        final List<BuildMessage1> ret = highlightServiceMessageTranslator.translate(sRunningBuild, buildMessage1, serviceMessage);

        assertThat(Arrays.asList(buildMessage1).equals(ret), is(true));
        verify(dataStoreService).saveData(eq(sRunningBuild), eq(TEST_MAP));
    }

    @Test
    public void testTranslate_empty_msg_attributes() throws Exception
    {
        when(serviceMessage.getAttributes()).thenReturn(new HashMap<String, String>(0));
        final List<BuildMessage1> ret = highlightServiceMessageTranslator.translate(sRunningBuild, buildMessage1, serviceMessage);

        assertThat(Arrays.asList(buildMessage1).equals(ret), is(true));
        verify(dataStoreService, never()).saveData(eq(sRunningBuild), eq(new HashMap<String, String>(0)));
    }

    @Test
    public void testGetServiceMessageName() throws Exception
    {
        highlightServiceMessageTranslator.setMessageName("highlight");

        assertThat(highlightServiceMessageTranslator.getServiceMessageName(), is("highlight"));
    }
}
