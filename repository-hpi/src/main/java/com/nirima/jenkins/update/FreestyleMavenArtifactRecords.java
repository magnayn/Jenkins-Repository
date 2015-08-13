package com.nirima.jenkins.update;

import java.util.ArrayList;
import java.util.List;

import hudson.maven.reporters.MavenArtifact;
import hudson.maven.reporters.MavenArtifactRecord;
import hudson.model.Action;

/**
 * Created by magnayn on 12/08/2015.
 */
public class FreestyleMavenArtifactRecords implements Action {

  public List<ExtendedMavenArtifactRecord> recordList = new ArrayList<ExtendedMavenArtifactRecord>();

  @Override
  public String getIconFileName() {
    return null;
  }

  @Override
  public String getDisplayName() {
    return "Artifacts";
  }

  @Override
  public String getUrlName() {
    return null;
  }

}
