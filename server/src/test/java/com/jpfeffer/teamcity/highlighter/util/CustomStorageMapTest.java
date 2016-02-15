package com.jpfeffer.teamcity.highlighter.util;

import com.jpfeffer.teamcity.highlighter.domain.Block;
import com.jpfeffer.teamcity.highlighter.domain.Level;
import com.jpfeffer.teamcity.highlighter.domain.Order;
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
 * <p>Tests proper functionality of {@link com.jpfeffer.teamcity.highlighter.util.CustomStorageMap} class.</p>
 *
 * @author jpfeffer
 * @since 2/2/15
 */
@RunWith(MockitoJUnitRunner.class)
public class CustomStorageMapTest
{
    private CustomStorageMap customStorageMap;

    @Mock
    private Map<String, String> map;

    @Before
    public void setUp() throws Exception
    {
        customStorageMap = new CustomStorageMap(map);
    }

    @Test
    public void testGetAsList() throws Exception
    {
        when(map.get(eq("text"))).thenReturn("a::b::c)");
        final Collection<String> collection = customStorageMap.getAsList("text");

        assertThat(collection.size(), is(3));
    }

    @Test
    public void testPut() throws Exception
    {
        customStorageMap.put("Intermittent tests", "testA");

        verify(map).put(eq("Intermittent tests"), eq("testA"));
    }

    @Test
    public void testPut_existing() throws Exception
    {
        when(map.containsKey(eq("Intermittent tests"))).thenReturn(true);
        when(map.get(eq("Intermittent tests"))).thenReturn("testA");
        customStorageMap.put("Intermittent tests", "testB");

        verify(map).put(eq("Intermittent tests"), eq("testA::testB"));
    }

    @Test
    public void testPut_existing_with_level() throws Exception
    {
        when(map.containsKey(eq("Intermittent tests::error"))).thenReturn(true);
        when(map.get(eq("Intermittent tests::error"))).thenReturn("testA");
        new CustomStorageMap(map, Level.error, null, null).put("Intermittent tests", "testB");

        verify(map).put(eq("Intermittent tests::error"), eq("testA::testB"));
    }

    @Test
    public void testPut_existing_with_block() throws Exception
    {
        when(map.containsKey(eq("Intermittent tests::collapsed"))).thenReturn(true);
        when(map.get(eq("Intermittent tests::collapsed"))).thenReturn("testA");
        new CustomStorageMap(map, null, Block.collapsed, null).put("Intermittent tests", "testB");

        verify(map).put(eq("Intermittent tests::collapsed"), eq("testA::testB"));
    }

    @Test
    public void testPut_existing_with_order() throws Exception
    {
        when(map.containsKey(eq("Intermittent tests::alphabet"))).thenReturn(true);
        when(map.get(eq("Intermittent tests::alphabet"))).thenReturn("testA");
        new CustomStorageMap(map, null, null, Order.alphabet).put("Intermittent tests", "testB");

        verify(map).put(eq("Intermittent tests::alphabet"), eq("testA::testB"));
    }

    @Test
    public void testPut_existing_with_level_and_block_and_order() throws Exception
    {
        when(map.containsKey(eq("Intermittent tests::error::collapsed::alphabet"))).thenReturn(true);
        when(map.get(eq("Intermittent tests::error::collapsed::alphabet"))).thenReturn("testA");
        new CustomStorageMap(map, Level.error, Block.collapsed, Order.alphabet).put("Intermittent tests", "testB");

        verify(map).put(eq("Intermittent tests::error::collapsed::alphabet"), eq("testA::testB"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPut_null_key() throws Exception
    {
        customStorageMap.put(null, "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPut_key_with_separator() throws Exception
    {
        customStorageMap.put("myKey::withSeparator", "value");
    }

    @Test
    public void testParseData() throws Exception
    {
        final String[] data = CustomStorageMap.parseData("Intermittent Tests::warn::collapsed::alphabet");

        assertThat(data.length, is(4));
        assertThat(data[CustomStorageMap.KEY_INDEX], is("Intermittent Tests"));
        assertThat(data[CustomStorageMap.LEVEL_INDEX], is("warn"));
        assertThat(data[CustomStorageMap.BLOCK_INDEX], is("collapsed"));
        assertThat(data[CustomStorageMap.ORDER_INDEX], is("alphabet"));
    }

    @Test
    public void testParseData_no_block() throws Exception
    {
        final String[] data = CustomStorageMap.parseData("Intermittent Tests::warn");

        assertThat(data.length, is(4));
        assertThat(data[CustomStorageMap.KEY_INDEX], is("Intermittent Tests"));
        assertThat(data[CustomStorageMap.LEVEL_INDEX], is("warn"));
        assertThat(data[CustomStorageMap.BLOCK_INDEX], is("expanded"));
        assertThat(data[CustomStorageMap.ORDER_INDEX], is("none"));
    }

    @Test
    public void testParseData_no_level() throws Exception
    {
        final String[] data = CustomStorageMap.parseData("Intermittent Tests::collapsed");

        assertThat(data.length, is(4));
        assertThat(data[CustomStorageMap.KEY_INDEX], is("Intermittent Tests"));
        assertThat(data[CustomStorageMap.LEVEL_INDEX], is("info"));
        assertThat(data[CustomStorageMap.BLOCK_INDEX], is("collapsed"));
        assertThat(data[CustomStorageMap.ORDER_INDEX], is("none"));
    }

    @Test
    public void testParseData_no_order() throws Exception
    {
        final String[] data = CustomStorageMap.parseData("Intermittent Tests::collapsed");

        assertThat(data.length, is(4));
        assertThat(data[CustomStorageMap.KEY_INDEX], is("Intermittent Tests"));
        assertThat(data[CustomStorageMap.LEVEL_INDEX], is("info"));
        assertThat(data[CustomStorageMap.BLOCK_INDEX], is("collapsed"));
        assertThat(data[CustomStorageMap.ORDER_INDEX], is("none"));
    }

    @Test
    public void testParseData_default() throws Exception
    {
        final String[] data = CustomStorageMap.parseData("Intermittent Tests");

        assertThat(data.length, is(4));
        assertThat(data[CustomStorageMap.KEY_INDEX], is("Intermittent Tests"));
        assertThat(data[CustomStorageMap.LEVEL_INDEX], is("info"));
        assertThat(data[CustomStorageMap.BLOCK_INDEX], is("expanded"));
        assertThat(data[CustomStorageMap.ORDER_INDEX], is("none"));
    }
}
