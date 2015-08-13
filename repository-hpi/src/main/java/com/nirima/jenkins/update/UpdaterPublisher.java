package com.nirima.jenkins.update;

import org.kohsuke.stapler.DataBoundConstructor;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.maven.MavenBuildProxy;
import hudson.maven.reporters.MavenArtifact;
import hudson.maven.reporters.MavenArtifactRecord;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import jenkins.model.ArtifactManager;

/**
 * Archive all the items that we were told about.
 */
public class UpdaterPublisher extends Recorder implements Serializable {

  @Override
  public BuildStepMonitor getRequiredMonitorService() {
    return BuildStepMonitor.BUILD;
  }


  @DataBoundConstructor
  public UpdaterPublisher() {

  }

  public static class Execution {
    final AbstractBuild<?, ?> build;
    final Launcher launcher;
    final BuildListener listener;
    ArtifactManager am;


    public Execution(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
        throws IOException {
      this.build = build;
      this.launcher = launcher;
      this.listener = listener;
      am = build.pickArtifactManager();
    }

    public void execute() throws IOException, InterruptedException {

      Map<String,String> artifacts = new HashMap<String,String>();

      FreestyleMavenArtifactRecords mar = build.getAction(FreestyleMavenArtifactRecords.class);
      if( mar == null )
        return;

      FilePath workspace = build.getWorkspace();


      for(ExtendedMavenArtifactRecord xmar : mar.recordList)
      {
        for(MavenArtifact a : xmar.fileMap.keySet()) {
          File f = xmar.fileMap.get(a);

          // Archive area - > workspace path
          String archivePath = a.groupId + "/" + a.artifactId + "/" + a.version + "/" + a.canonicalName;
          artifacts.put(archivePath, f.toString().substring(workspace.toString().length()+1) );
        }
      }


      am.archive(workspace, launcher, listener, artifacts);

    }
  }

  @Override
  public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
      throws InterruptedException, IOException {

    Execution e = new Execution(build, launcher, listener);

    try {
      e.execute();
    } catch(Exception ex) {
      ex.printStackTrace();
      return false;
    }

    return true;


  }


  @Extension
  public static final class DescriptorImpl extends
                                           BuildStepDescriptor<Publisher> {

    @Override
    public boolean isApplicable(Class<? extends AbstractProject> jobType) {
      return true;
    }

    @Override
    public String getDisplayName() {
      return "Publish Maven Artifacts";
    }
  }
  }
