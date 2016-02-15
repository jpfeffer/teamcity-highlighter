package com.jpfeffer.teamcity.highlighter.util;

import com.jpfeffer.teamcity.highlighter.domain.HighlightData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashMap;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * <p>Tests proper functionality of {@link com.jpfeffer.teamcity.highlighter.util.HighlightDataMap} class.</p>
 *
 * @author jpfeffer
 * @since 2/12/16
 */
@RunWith(MockitoJUnitRunner.class)
public class HighlightDataMapTest
{
    private HighlightDataMap highlightDataMap;

    @Mock
    private HighlightData highlightData;
    @Mock
    private HighlightData highlightData2;

    @Before
    public void setUp() throws Exception
    {
        highlightDataMap = new HighlightDataMap();
        highlightDataMap.put("key1", highlightData);

        when(highlightData.getValues()).thenReturn(Arrays.asList("val1", "val2"));
        when(highlightData2.getValues()).thenReturn(Arrays.asList("val3", "val4"));
    }

    @Test
    public void testContainsKey() throws Exception
    {
        assertThat(highlightDataMap.containsKey("key1"), is(true));
    }

    @Test
    public void testContainsValue() throws Exception
    {
        assertThat(highlightDataMap.containsValue(highlightData), is(true));
    }

    @Test
    public void testGet() throws Exception
    {
        assertThat(highlightDataMap.get("key1"), is(highlightData));
    }

    @Test
    public void testPut() throws Exception
    {
        highlightDataMap.put("new_key", highlightData2);
        assertThat(highlightDataMap.containsKey("new_key"), is(true));
        assertThat(highlightDataMap.get("new_key"), is(highlightData2));
    }

    @Test
    public void testPut_existing() throws Exception
    {
        highlightDataMap.put("key1", highlightData2);
        assertThat(highlightDataMap.get("key1").getValues().size(), is(4));
    }

    @Test
    public void testRemove() throws Exception
    {
        assertThat(highlightDataMap.remove("key1"), is(highlightData));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testPutAll() throws Exception
    {
        highlightDataMap.putAll(new HashMap<String, HighlightData>(0));
    }

    @Test
    public void testClear() throws Exception
    {
        highlightDataMap.clear();
        assertThat(highlightDataMap.size(), is(0));
    }

    @Test
    public void testKeySet() throws Exception
    {
        highlightDataMap.put("key2", highlightData2);
        assertThat(highlightDataMap.keySet().size(), is(2));
    }

    @Test
    public void testValues() throws Exception
    {
        highlightDataMap.put("key2", highlightData2);
        assertThat(highlightDataMap.values().size(), is(2));
    }

    @Test
    public void testEntrySet() throws Exception
    {
        highlightDataMap.put("key2", highlightData2);
        assertThat(highlightDataMap.entrySet().size(), is(2));
    }
}