package com.jpfeffer.teamcity.highlighter.message;

/**
 * <p>Enumeration of keys that will be taken into account and translated when highlight message has been sent.</p>
 *
 * @author jpfeffer
 * @since 1/29/15
 */
public enum HighlightMessageKey
{
    /**
     * Serves as a key when {@link jetbrains.buildServer.serverSide.CustomDataStorage} is used.
     */
    title,

    /**
     * Text of the message to be shown on TeamCity using {@link com.jpfeffer.teamcity.highlighter.extension.HighlighterPageExtension#}
     * extension page.
     */
    text,

    /**
     * Level of the message, only values listed in {@link com.jpfeffer.teamcity.highlighter.domain.Level} are
     * accepted.
     */
    level,

    /**
     * Style of the text block to be shown.
     */
    block
}
