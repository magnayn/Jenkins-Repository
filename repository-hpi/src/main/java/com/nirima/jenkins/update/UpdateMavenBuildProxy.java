package com.nirima.jenkins.update;

import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import hudson.FilePath;
import hudson.maven.ExecutedMojo;
import hudson.maven.MavenBuildInformation;
import hudson.maven.MavenBuildProxy;
import hudson.maven.MavenProjectActionBuilder;
import hudson.maven.MavenReporter;
import hudson.model.AbstractBuild;
import hudson.model.Result;

/**
 * Created by magnayn on 12/08/2015.
 */
public class UpdateMavenBuildProxy implements MavenBuildProxy {

  private final Map<String,String> artifacts = new LinkedHashMap<String,String>();

  public UpdateMavenBuildProxy(AbstractBuild<?, ?> build) {
  }

  @Override
  public <V, T extends Throwable> V execute(BuildCallable<V, T> program)
      throws T, IOException, InterruptedException {
    return null;
  }

  @Override
  public void executeAsync(BuildCallable<?, ?> program) throws IOException {

  }

  @Override
  public FilePath getRootDir() {
    return null;
  }

  @Override
  public FilePath getProjectRootDir() {
    return null;
  }

  @Override
  public FilePath getModuleSetRootDir() {
    return null;
  }

  @Override
  public FilePath getArtifactsDir() {
    return null;
  }

  @Override
  public void queueArchiving(String artifactPath, String artifact) {
    artifacts.put(artifactPath, artifact);
  }



  @Override
  public void setResult(Result result) {

  }

  @Override
  public Calendar getTimestamp() {
    return null;
  }

  @Override
  public long getMilliSecsSinceBuildStart() {
    return 0;
  }

  @Override
  public boolean isArchivingDisabled() {
    return false;
  }

  @Override
  public void registerAsProjectAction(MavenReporter reporter) {

  }

  @Override
  public void registerAsProjectAction(MavenProjectActionBuilder builder) {

  }

  @Override
  public void registerAsAggregatedProjectAction(MavenReporter reporter) {

  }

  @Override
  public void setExecutedMojos(List<ExecutedMojo> executedMojos) {

  }

  @Override
  public MavenBuildInformation getMavenBuildInformation() {
    return null;
  }
}
