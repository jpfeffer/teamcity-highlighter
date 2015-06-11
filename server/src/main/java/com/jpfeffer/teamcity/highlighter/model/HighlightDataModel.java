package com.jpfeffer.teamcity.highlighter.model;

import com.jpfeffer.teamcity.highlighter.domain.Block;
import com.jpfeffer.teamcity.highlighter.domain.HighlightData;
import com.jpfeffer.teamcity.highlighter.domain.HighlightDataMap;
import com.jpfeffer.teamcity.highlighter.domain.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.jpfeffer.teamcity.highlighter.domain.HighlightDataMap.getKeyData;
import static com.jpfeffer.teamcity.highlighter.domain.HighlightDataMap.BLOCK_INDEX;
import static com.jpfeffer.teamcity.highlighter.domain.HighlightDataMap.KEY_INDEX;
import static com.jpfeffer.teamcity.highlighter.domain.HighlightDataMap.LEVEL_INDEX;
import static com.jpfeffer.teamcity.highlighter.util.Util.valueOfOrDefault;

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

    public HighlightDataModel(Map<String, String> messageData)
    {
        highlightData = new ArrayList<>();

        if (messageData != null && !messageData.isEmpty())
        {
            final HighlightDataMap highlightDataMap = new HighlightDataMap(messageData);
            for (final String key : highlightDataMap.keySet())
            {
                final String[] keyData = getKeyData(key);

                highlightData.add(new HighlightData(keyData[KEY_INDEX], highlightDataMap.getAsCollection(key),
                                                    valueOfOrDefault(keyData[LEVEL_INDEX], Level.class, Level.info),
                                                    valueOfOrDefault(keyData[BLOCK_INDEX], Block.class, Block.expanded)));
            }
        }

        Collections.sort(highlightData, new Comparator<HighlightData>()
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
