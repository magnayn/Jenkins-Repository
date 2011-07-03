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
import hudson.model.BuildableItemWithBuildWrappers;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.plugins.git.util.BuildChooserDescriptor;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: magnayn
 * Date: 01/07/2011
 * Time: 17:41
 * To change this template use File | Settings | File Templates.
 */
public class SelectionTypeProject extends SelectionType {
    public String project;
    public String buildId;

    @DataBoundConstructor
    public SelectionTypeProject(String project, String buildId) {
        this.project = project;
        this.buildId = buildId;
    }



    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getBuildId() {
        return buildId;
    }

    public void setBuildId(String buildId) {
        this.buildId = buildId;
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
