package com.nirima.jenkins;

import hudson.Extension;
import hudson.model.Descriptor;
import org.kohsuke.stapler.DataBoundConstructor;


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

    @Extension
    public static final class DescriptorImpl extends Descriptor<SelectionType> {

         @Override
         public String getDisplayName() {
             return "Specified Path in Repository";  //To change body of implemented methods use File | Settings | File Templates.
         }
     }
}
