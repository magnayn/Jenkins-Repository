package com.nirima.jenkins.repo;

import hudson.ExtensionList;
import hudson.ExtensionPoint;
import jenkins.model.Jenkins;
/**
 * Extension point for repositories
 */
public abstract class RepositoryExtensionPoint implements ExtensionPoint {

    private RepositoryElement repositoryRoot;

    public static ExtensionList<RepositoryExtensionPoint> all() {
        return Jenkins.getInstance().getExtensionList(RepositoryExtensionPoint.class);
    }

    public abstract RepositoryElement getRepositoryRoot(RootElement rootElement);
}
