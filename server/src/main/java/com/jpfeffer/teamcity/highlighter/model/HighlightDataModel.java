package com.jpfeffer.teamcity.highlighter.model;

import com.jpfeffer.teamcity.highlighter.domain.HighlightData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * <p>Model holding highlight data for TeamCity UI.</p>
 *
 * <p>Data are sorted from the highest level ({@link com.jpfeffer.teamcity.highlighter.domain.Level#error}) to the
 * lowest one (({@link com.jpfeffer.teamcity.highlighter.domain.Level#info})) and key.</p>
 *
 * <p>See <code>highlighter.jsp</code> page for more details on how the data are presented.</p>
 *
 * @author jpfeffer
 * @since 1/28/15
 */
public class HighlightDataModel
{
    private final List<HighlightData> highlightData;

    public HighlightDataModel(Collection<HighlightData> highlightData)
    {
        this.highlightData = new ArrayList<>(highlightData);

        Collections.sort(this.highlightData, new Comparator<HighlightData>()
        {
            @Override
            public int compare(HighlightData o1, HighlightData o2)
            {

                final int i = o1.getLevel().compareTo(o2.getLevel());
                if (i != 0)
                {
                    return i;
                }
                return o1.getKey().compareTo(o2.getKey());
            }
        });
    }

    public List<HighlightData> getHighlightData()
    {
        return highlightData;
    }
}
