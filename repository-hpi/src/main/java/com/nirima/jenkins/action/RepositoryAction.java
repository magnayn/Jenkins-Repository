package com.nirima.jenkins.action;

import hudson.model.Action;
import jenkins.model.Jenkins;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This is a Jenkins action that gets added to the build to denote what
 * the upstream repository that was used when generating this build.
 */
public abstract class RepositoryAction implements Action, Serializable, Cloneable {
    public abstract URL getUrl() throws MalformedURLException;

    public String getDisplayName() {
        return null;
    }
    public String getIconFileName() {
        return null;
    }
    public String getUrlName() {
        return null;
    }
}
