/*
 * The MIT License
 *
 * Copyright (c) 2011, Nigel Magnay / NiRiMa
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.nirima.jenkins;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.BuildableItemWithBuildWrappers;
import hudson.model.Descriptor;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.DataBoundConstructor;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


public class SelectionTypeProject extends SelectionType {
    public String project;
    public String build;

    @DataBoundConstructor
    public SelectionTypeProject(String project, String build) {
        this.project = project;
        this.build = build;
    }


    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getBuild() {
        return build;
    }

    public void setBuild(String buildId) {
        this.build = buildId;
    }

    @Override
    public URL getUrl(AbstractBuild theBuild) throws MalformedURLException, RepositoryDoesNotExistException {
        URL url = new URL(Jenkins.getInstance().getRootUrl());

        url = new URL(url, "plugin/repository/project/");

        // Specific
        url = new URL(url, project + "/");


        url = addBuildId(url, build);

        return url;
    }

    @Extension
    public static final class DescriptorImpl extends Descriptor<SelectionType> {

        @Override
        public String getDisplayName() {
            return "Project";  //To change body of implemented methods use File | Settings | File Templates.
        }

        public List<BuildableItemWithBuildWrappers> getJobs() {
            return Jenkins.getInstance().getAllItems(BuildableItemWithBuildWrappers.class);
        }
    }

}
