package com.jpfeffer.teamcity.highlighter.service;

import com.jpfeffer.teamcity.highlighter.domain.HighlightData;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.CompactWriter;
import jetbrains.buildServer.ArtifactsConstants;
import jetbrains.buildServer.messages.XStreamHolder;
import jetbrains.buildServer.serverSide.SBuild;
import jetbrains.buildServer.util.FileUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * This storage stores HighlightData under the build hidden artifacts:
 * .teamcity/pluginData/highlighter/
 */
public class ArtifactsBasedDataStoreService implements DataStoreService<HighlightData> {
  private static final Logger LOG = Logger.getLogger(ArtifactsBasedDataStoreService.class.getName());

  private final XStreamHolder myXStreamHolder = new XStreamHolder();
  public final static String DATA_FILE_PATH = ArtifactsConstants.TEAMCITY_ARTIFACTS_DIR + "/pluginData/highlighter/highlighter.xml";

  @Override
  public void saveData(@NotNull SBuild sBuild, HighlightData data) {
    List<HighlightData> currentData = loadData(sBuild);
    boolean found = false;
    for (int i=0; i<currentData.size(); i++) {
      HighlightData cur = currentData.get(i);
      if (cur.getKey().equals(data.getKey())) {
        currentData.set(i, data);
        found = true;
        break;
      }
    }

    if (!found) {
      currentData.add(data);
    }

    final StringWriter writer = new StringWriter();
    getXStream().marshal(currentData, new CompactWriter(writer));
    String serialized = writer.toString();

    final File filePath = getDataFilePath(sBuild);
    final File parentFile = filePath.getParentFile();
    if (!parentFile.isDirectory()) {
      if (!parentFile.mkdirs()) {
        LOG.warning("Could not create directory for highlighter plugin data: " + parentFile.getAbsolutePath());
      }
    }
    try {
      final File newFile = new File(filePath.getParent(), filePath.getName() + ".new"); // save as new to prevent existing data corruption if there is a lack of disk space
      FileUtil.writeFile(newFile, serialized, "UTF-8");
      FileUtil.rename(newFile, filePath);
    } catch (Exception e) {
      LOG.warning("Could not save highlighter plugin data under the build artifacts directory: " + filePath.getAbsolutePath() + ", error: " + e.toString());
    }
  }

  private XStream getXStream() {
    return myXStreamHolder.getXStream(getClass().getClassLoader());
  }

  @NotNull
  private File getDataFilePath(@NotNull SBuild sBuild) {
    return new File(sBuild.getArtifactsDirectory(), DATA_FILE_PATH);
  }

  @Override
  public List<HighlightData> loadData(@NotNull SBuild sBuild) {
    final File filePath = getDataFilePath(sBuild);
    if (!filePath.isFile()) return new ArrayList<>();

    try {
      String data = FileUtil.readText(filePath, "UTF-8");
      //noinspection unchecked
      return (List<HighlightData>) getXStream().fromXML(data);
    } catch (Exception e) {
      LOG.warning("Could not load highlighter plugin data from the build artifacts directory: " + filePath.getAbsolutePath() + ", error: " + e.toString());
    }
    return new ArrayList<>();
  }
}
