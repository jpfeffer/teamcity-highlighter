package com.jpfeffer.teamcity.highlighter.domain;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * <p>Tests proper functionality of {@link HighlightDataMap} class.</p>
 *
 * @author jpfeffer
 * @since 2/2/15
 */
@RunWith(MockitoJUnitRunner.class)
public class HighlightDataMapTest
{
    private HighlightDataMap highlightDataMap;

    @Mock
    private Map<String, String> map;

    @Before
    public void setUp() throws Exception
    {
        highlightDataMap = new HighlightDataMap(map);
    }

    @Test
    public void testGetAsCollection() throws Exception
    {
        when(map.get(eq("text"))).thenReturn("a::b::c)");
        final Collection<String> collection = highlightDataMap.getAsCollection("text");

        assertThat(collection.size(), is(3));
    }

    @Test
    public void testPut() throws Exception
    {
        highlightDataMap.put("Intermittent tests", "testA");

        verify(map).put(eq("Intermittent tests"), eq("testA"));
    }

    @Test
    public void testPut_existing() throws Exception
    {
        when(map.containsKey(eq("Intermittent tests"))).thenReturn(true);
        when(map.get(eq("Intermittent tests"))).thenReturn("testA");
        highlightDataMap.put("Intermittent tests", "testB");

        verify(map).put(eq("Intermittent tests"), eq("testA::testB"));
    }

    @Test
    public void testPut_existing_with_level() throws Exception
    {
        when(map.containsKey(eq("Intermittent tests::error"))).thenReturn(true);
        when(map.get(eq("Intermittent tests::error"))).thenReturn("testA");
        new HighlightDataMap(map, Level.error, null).put("Intermittent tests", "testB");

        verify(map).put(eq("Intermittent tests::error"), eq("testA::testB"));
    }

    @Test
    public void testPut_existing_with_block() throws Exception
    {
        when(map.containsKey(eq("Intermittent tests::collapsed"))).thenReturn(true);
        when(map.get(eq("Intermittent tests::collapsed"))).thenReturn("testA");
        new HighlightDataMap(map, null, Block.collapsed).put("Intermittent tests", "testB");

        verify(map).put(eq("Intermittent tests::collapsed"), eq("testA::testB"));
    }

    @Test
    public void testPut_existing_with_level_and_block() throws Exception
    {
        when(map.containsKey(eq("Intermittent tests::error::collapsed"))).thenReturn(true);
        when(map.get(eq("Intermittent tests::error::collapsed"))).thenReturn("testA");
        new HighlightDataMap(map, Level.error, Block.collapsed).put("Intermittent tests", "testB");

        verify(map).put(eq("Intermittent tests::error::collapsed"), eq("testA::testB"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPut_null_key() throws Exception
    {
        highlightDataMap.put(null, "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPut_key_with_separator() throws Exception
    {
        highlightDataMap.put("myKey::withSeparator", "value");
    }

    @Test
    public void testSplitKey() throws Exception
    {
        final String[] data = HighlightDataMap.getKeyData("Intermittent Tests::warn::collapsed");

        assertThat(data.length, is(3));
        assertThat(data[HighlightDataMap.KEY_INDEX], is("Intermittent Tests"));
        assertThat(data[HighlightDataMap.LEVEL_INDEX], is("warn"));
        assertThat(data[HighlightDataMap.BLOCK_INDEX], is("collapsed"));
    }

    @Test
    public void testSplitKey_no_block() throws Exception
    {
        final String[] data = HighlightDataMap.getKeyData("Intermittent Tests::warn");

        assertThat(data.length, is(3));
        assertThat(data[HighlightDataMap.KEY_INDEX], is("Intermittent Tests"));
        assertThat(data[HighlightDataMap.LEVEL_INDEX], is("warn"));
        assertThat(data[HighlightDataMap.BLOCK_INDEX], is("expanded"));
    }

    @Test
    public void testSplitKey_no_level() throws Exception
    {
        final String[] data = HighlightDataMap.getKeyData("Intermittent Tests::collapsed");

        assertThat(data.length, is(3));
        assertThat(data[HighlightDataMap.KEY_INDEX], is("Intermittent Tests"));
        assertThat(data[HighlightDataMap.LEVEL_INDEX], is("info"));
        assertThat(data[HighlightDataMap.BLOCK_INDEX], is("collapsed"));
    }

    @Test
    public void testSplitKey_default() throws Exception
    {
        final String[] data = HighlightDataMap.getKeyData("Intermittent Tests");

        assertThat(data.length, is(3));
        assertThat(data[HighlightDataMap.KEY_INDEX], is("Intermittent Tests"));
        assertThat(data[HighlightDataMap.LEVEL_INDEX], is("info"));
        assertThat(data[HighlightDataMap.BLOCK_INDEX], is("expanded"));
    }
}
