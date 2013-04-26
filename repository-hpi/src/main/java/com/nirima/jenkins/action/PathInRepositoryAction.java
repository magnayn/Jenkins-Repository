package com.nirima.jenkins.action;

import jenkins.model.Jenkins;

import java.net.MalformedURLException;
import java.net.URL;

public class PathInRepositoryAction extends RepositoryAction {

    String subPath;

    public PathInRepositoryAction(String subPath) {
        this.subPath = subPath;
    }

    @Override
    public URL getUrl() throws MalformedURLException {

        URL url = new URL(Jenkins.getInstance().getRootUrl());

        url = new URL(url, "plugin/repository");

        url = new URL(url, subPath);

        return url;
    }

}
