package com.nirima.jenkins.action;

import jenkins.model.Jenkins;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Action storing build repository URL.
 */
public class ProjectRepositoryAction extends RepositoryAction {
    private static final long serialVersionUID = 1L;

    String projectName;
    int    buildNumber;

    private String urlSuffix;


    public ProjectRepositoryAction(String project, int id, String s) {
        projectName = project;
        buildNumber = id;
        urlSuffix = s;
    }



    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public int getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(int buildNumber) {
        this.buildNumber = buildNumber;
    }

    public URL getUrl() throws MalformedURLException {
        URL url = new URL(Jenkins.getInstance().getRootUrl());

        url = new URL(url, "plugin/repository/project/");

        url = new URL(url, projectName + "/Build/" + buildNumber + "/" + (urlSuffix!=null?urlSuffix:"") );

        return url;

    }
}
