package com.nirima.jenkins;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.Descriptor;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.DataBoundConstructor;

import java.net.MalformedURLException;
import java.net.URL;


public class SelectionTypeSpecified extends SelectionType  {
    public String path;

    @DataBoundConstructor
    public SelectionTypeSpecified(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public URL getUrl(AbstractBuild build) throws MalformedURLException, RepositoryDoesNotExistException {
         URL url = new URL(Jenkins.getInstance().getRootUrl());

        url = new URL(url, "plugin/repository/project/");


        url = new URL(url, path);

        return url;
    }

    @Extension
    public static final class DescriptorImpl extends Descriptor<SelectionType> {

         @Override
         public String getDisplayName() {
             return "Specified Path in Repository";  //To change body of implemented methods use File | Settings | File Templates.
         }
     }
}
