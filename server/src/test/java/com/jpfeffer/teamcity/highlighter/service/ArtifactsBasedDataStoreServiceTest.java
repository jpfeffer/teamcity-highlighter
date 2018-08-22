package com.jpfeffer.teamcity.highlighter.service;

import com.jpfeffer.teamcity.highlighter.domain.Block;
import com.jpfeffer.teamcity.highlighter.domain.HighlightData;
import com.jpfeffer.teamcity.highlighter.domain.Level;
import jetbrains.buildServer.TempFiles;
import jetbrains.buildServer.serverSide.SBuild;
import jetbrains.buildServer.serverSide.SBuildType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import jetbrains.buildServer.util.FileUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.util.Collection;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ArtifactsBasedDataStoreServiceTest {
  private DataStoreService<HighlightData> dataStoreService;

  @Mock
  private SBuild sBuild;
  @Mock
  private SBuildType sBuildType;
  @Mock
  private HighlightData highlightData;

  private TempFiles tempFiles;
  private File buildArtifactsDir;

  @Before
  public void setUp() throws Exception {
    tempFiles = new TempFiles();

    dataStoreService = new ArtifactsBasedDataStoreService();
    when(sBuild.getBuildId()).thenReturn(1234L);
    when(sBuild.getBuildType()).thenReturn(sBuildType);
    buildArtifactsDir = tempFiles.createTempDir();
    when(sBuild.getArtifactsDirectory()).thenReturn(buildArtifactsDir);
  }

  @Test
  public void testSaveData() throws Exception
  {
    when(highlightData.getKey()).thenReturn("Some title");
    when(highlightData.getSingleValue()).thenReturn("Some text");
    when(highlightData.getLevel()).thenReturn(Level.error);
    when(highlightData.getBlock()).thenReturn(Block.collapsed);
    dataStoreService.saveData(sBuild, highlightData);

    File highlighterXml = new File(buildArtifactsDir, ArtifactsBasedDataStoreService.DATA_FILE_PATH);
    assertTrue(highlighterXml.isFile());

    final Collection<HighlightData> data = dataStoreService.loadData(sBuild);
    assertEquals(1, data.size());
    final HighlightData loadedData = data.iterator().next();
    assertEquals("Some title", loadedData.getKey());
    assertEquals("Some text", loadedData.getSingleValue());
  }

  @After
  public void tearDown() {
    tempFiles.cleanup();
  }

}
