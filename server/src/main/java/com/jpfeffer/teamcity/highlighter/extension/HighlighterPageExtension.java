package com.jpfeffer.teamcity.highlighter.extension;

import com.jpfeffer.teamcity.highlighter.model.HighlightDataModel;
import com.jpfeffer.teamcity.highlighter.service.DataStoreService;
import jetbrains.buildServer.serverSide.SBuild;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.SimplePageExtension;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.logging.Logger;

import static jetbrains.buildServer.web.openapi.PlaceId.BUILD_RESULTS_FRAGMENT;

/**
 * <p>{@link jetbrains.buildServer.web.openapi.SimplePageExtension} implementation that extends TeamCity UI with a new
 * content at build overview fragment by using {@link jetbrains.buildServer.web.openapi.PlaceId#BUILD_RESULTS_FRAGMENT}
 * position.</p>
 *
 * <p>The content is visible only if there are any data loaded for the current build.</p>
 *
 * @author jpfeffer
 * @since 1/26/15
 */
public class HighlighterPageExtension extends SimplePageExtension
{
    private static final Logger LOG = Logger.getLogger(HighlighterPageExtension.class.getName());
    private static final String PLUGIN_ID = "highlighter-plugin";

    private final SBuildServer sBuildServer;
    private HighlightDataModel highlightDataModel;

    @Resource
    private DataStoreService<Map<String, String>> dataStoreService;

    public HighlighterPageExtension(@NotNull PagePlaces pagePlaces, @NotNull SBuildServer sBuildServer)
    {
        super(pagePlaces, BUILD_RESULTS_FRAGMENT, PLUGIN_ID, "web/highlighter.jsp");
        this.sBuildServer = sBuildServer;
    }

    @Override
    public void fillModel(@NotNull Map<String, Object> model, @NotNull HttpServletRequest request)
    {
        super.fillModel(model, request);

        model.put("pluginId", PLUGIN_ID);
        model.put("model", highlightDataModel);
    }

    @Override
    public boolean isAvailable(@NotNull HttpServletRequest request)
    {
        final Long buildId;
        try
        {
            buildId = Long.valueOf(request.getParameter("buildId"));
        }
        catch (NumberFormatException e)
        {
            LOG.warning(String.format("'%s' is not a valid build id, highlighter extension won't be available", request.getParameter("buildId")));
            return false;
        }
        final SBuild sBuild = sBuildServer.findBuildInstanceById(buildId);
        highlightDataModel = new HighlightDataModel(dataStoreService.loadData(sBuild));
        return !highlightDataModel.getHighlightData().isEmpty();
    }
}
