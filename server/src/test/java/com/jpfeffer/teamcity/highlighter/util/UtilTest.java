package com.jpfeffer.teamcity.highlighter.util;

import com.jpfeffer.teamcity.highlighter.domain.Block;
import com.jpfeffer.teamcity.highlighter.domain.Level;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class UtilTest
{
    @Test
    public void testValueOfOrNull() throws Exception
    {
        assertThat(Util.valueOfOrNull("error", Level.class), is(Level.error));
        assertThat(Util.valueOfOrNull("null", Level.class), is(nullValue()));
    }

    @Test
    public void testValueOfOrDefault() throws Exception
    {
        assertThat(Util.valueOfOrDefault("collapsed", Block.class, Block.expanded), is(Block.collapsed));
        assertThat(Util.valueOfOrDefault("null", Block.class, Block.expanded), is(Block.expanded));
    }

    @Test
    public void testNameOfOrNull() throws Exception
    {
        assertThat(Util.nameOfOrNull("warn", Level.class), is("warn"));
        assertThat(Util.nameOfOrNull("null", Level.class), is(nullValue()));
    }
}