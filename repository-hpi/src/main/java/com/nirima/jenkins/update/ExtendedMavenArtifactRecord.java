package com.nirima.jenkins.update;

import java.io.File;
import java.util.List;
import java.util.Map;

import hudson.maven.MavenBuild;
import hudson.maven.reporters.MavenArtifact;
import hudson.maven.reporters.MavenArtifactRecord;

/**
 * Created by magnayn on 12/08/2015.
 */
public class ExtendedMavenArtifactRecord extends MavenArtifactRecord {

  // Where is each file in the local path?
  Map<MavenArtifact, File> fileMap;

  public ExtendedMavenArtifactRecord(MavenBuild parent,
                                     MavenArtifact pomArtifact,
                                     MavenArtifact mainArtifact,
                                     List<MavenArtifact> attachedArtifacts,
                                     String repositoryUrl, String repositoryId,
                                    Map<MavenArtifact,File> fileMap) {
    super(parent, pomArtifact, mainArtifact, attachedArtifacts, repositoryUrl, repositoryId);
    this.fileMap = fileMap;
  }
}
