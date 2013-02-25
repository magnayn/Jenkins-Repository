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

import com.nirima.jenkins.action.ProjectRepositoryAction;
import com.nirima.jenkins.action.RepositoryAction;
import hudson.Extension;
import hudson.model.*;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.DataBoundConstructor;

import java.net.MalformedURLException;
import java.net.URL;


public class SelectionTypeUpstream extends SelectionType {
    public String build;

    @DataBoundConstructor
    public SelectionTypeUpstream(String build) {
        this.build = build;
    }

    public String getBuild() {
        return build;
    }

    public void setBuild(String buildId) {
        this.build = build;
    }

    @Override
    public RepositoryAction getAction(AbstractBuild b) throws MalformedURLException, RepositoryDoesNotExistException {

        // What is the upstream project name?
        Cause.UpstreamCause theCause = (Cause.UpstreamCause) b.getCause(Cause.UpstreamCause.class);
        String theProject;
        int    theBuild;
        if (theCause == null) {
            ParametersAction action = b.getAction(ParametersAction.class);
            if (action == null) {
                throw new RepositoryDoesNotExistException();
            }
            RunParameterValue value = (RunParameterValue) action.getParameter("Upstream");

            theProject = value.getJobName();
            theBuild =   Integer.parseInt(value.getNumber());
        } else {
            theProject = theCause.getUpstreamProject();
            theBuild = theCause.getUpstreamBuild();
        }

        return new ProjectRepositoryAction(theProject, theBuild, this.build);


    }

    @Extension
    public static final class DescriptorImpl extends Descriptor<SelectionType> {

        @Override
        public String getDisplayName() {
            return "Upstream Project that triggered this build";
        }
    }
}
