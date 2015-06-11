package com.jpfeffer.teamcity.highlighter.message.translator;

import com.jpfeffer.teamcity.highlighter.service.DataStoreService;
import jetbrains.buildServer.messages.BuildMessage1;
import jetbrains.buildServer.messages.serviceMessages.ServiceMessage;
import jetbrains.buildServer.messages.serviceMessages.ServiceMessageTranslator;
import jetbrains.buildServer.serverSide.SRunningBuild;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * <p>{@link ServiceMessageTranslator} that catches TeamCity messages with name {@link
 * HighlightServiceMessageTranslator#messageName}. The translated text is then stored using {@link
 * DataStoreService#saveData(jetbrains.buildServer.serverSide.SBuild, Object)} service.</p>
 *
 * <p>Usage: <code>##teamcity[highlight title='Intermittent Tests' text="TimesheetsFeatureChangeMigrationTest.testFeatureCycle"
 * level="warn"]</code></p>
 *
 * <p><code>level</code> key is optional and if not specified, {@link com.jpfeffer.teamcity.highlighter.domain.Level#info}
 * will be used.</p>
 *
 * @author jpfeffer
 * @since 1/26/15
 */
public class HighlightServiceMessageTranslator implements ServiceMessageTranslator
{
    private static final Logger LOG = Logger.getLogger(HighlightServiceMessageTranslator.class.getName());

    private String messageName;

    @Resource
    private DataStoreService<Map<String, String>> dataStoreService;

    @NotNull
    @Override
    public List<BuildMessage1> translate(@NotNull SRunningBuild sRunningBuild, @NotNull BuildMessage1 buildMessage1,
                                         @NotNull ServiceMessage serviceMessage)
    {
        final Map<String, String> messageAttributes = serviceMessage.getAttributes();
        if (messageAttributes.isEmpty())
        {
            LOG.warning("Could not translate service message with empty attributes.");
            return Arrays.asList(buildMessage1);
        }

        dataStoreService.saveData(sRunningBuild, messageAttributes);

        return Arrays.asList(buildMessage1);
    }

    @NotNull
    @Override
    public String getServiceMessageName()
    {
        return messageName;
    }

    public void setMessageName(String messageName)
    {
        this.messageName = messageName;
    }
}
