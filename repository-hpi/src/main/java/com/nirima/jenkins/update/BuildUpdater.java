package com.nirima.jenkins.update;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hudson.maven.reporters.MavenArtifact;
import hudson.maven.reporters.MavenArtifactRecord;
import hudson.model.Job;
import hudson.model.Run;
import jenkins.model.Jenkins;

/**
 * Created by magnayn on 12/08/2015.
 */
public class BuildUpdater {

  Run build;
  private List<MavenArtifact> attachedArtifacts = new ArrayList<MavenArtifact>();
  private MavenArtifact mainArtifact;
  private MavenArtifact pomArtifact;
  private String repositoryUrl;
  private String repositoryId;
  private Map<MavenArtifact, File> fileMap = new HashMap<MavenArtifact, File>();

  public BuildUpdater(StaplerRequest req, StaplerResponse rsp) throws IOException {
    // Get the job
    InputStreamReader isr = new InputStreamReader(req.getInputStream());
    BufferedReader br = new BufferedReader(isr);

    String project = br.readLine();
    String buildNumber = br.readLine();

    for (Job j : Jenkins.getInstance().getAllItems(Job.class)) {
      if (j.getName().equals(project)) {
        // Correct job
        build = j.getBuildByNumber(Integer.parseInt(buildNumber));
      }
    }

    if (build == null) {
      throw new RuntimeException("Build was not found");
    }

    String line;
    while ((line = br.readLine()) != null) {
                                 if (line.equals("[pom]")) {
        pomArtifact = parseArtifact(br);
      } else if (line.equals("[main]")) {
        mainArtifact = parseArtifact(br);
      } else if (line.equals("[attached]")) {
        attachedArtifacts.add(parseArtifact(br));
      } else if (line.equals("[repositoryUrl]")) {
        repositoryUrl = br.readLine();
      } else if (line.equals("[repositoryId]")) {
        repositoryId = br.readLine();
      } else {
        System.out.println("Unexpected line: " + line);
      }
    }


  }

  private MavenArtifact parseArtifact(BufferedReader br) throws IOException {

    String file = br.readLine();
    String groupId = br.readLine();
    String artifactId = br.readLine();
    String version = br.readLine();
    String classifier = br.readLine();
    String type = br.readLine();
    String fileName = br.readLine();
    String md5sum = br.readLine();


    MavenArtifact a = new MavenArtifact(groupId, artifactId, version, classifier, type, fileName, md5sum);
    fileMap.put(a, new File(file) );
    return a;
  }

  public void execute() {

    FreestyleMavenArtifactRecords records = build.getAction(FreestyleMavenArtifactRecords.class);
    if( records == null )
      records = new FreestyleMavenArtifactRecords();


    ExtendedMavenArtifactRecord mar = new ExtendedMavenArtifactRecord(null,
                                                      pomArtifact,
                                                      mainArtifact,
                                                      attachedArtifacts,
                                                      repositoryUrl,
                                                      repositoryId,fileMap);
    records.recordList.add(mar);
    build.addAction(records);

  }
}
