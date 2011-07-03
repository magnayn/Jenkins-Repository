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
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.export.ExportedBean;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

@ExportedBean
public class RepositoryDefinitionProperty extends BuildWrapper implements Serializable {

    public SelectionType upstream;

    @DataBoundConstructor
    public RepositoryDefinitionProperty(SelectionType upstream) {
        this.upstream = upstream;
    }

    @Extension
    public static class DescriptorImpl extends BuildWrapperDescriptor {

        @Override
        public String getDisplayName() {
            return "Define Upstream Maven Repository";
        }

        @Override
        public boolean isApplicable(AbstractProject<?, ?> abstractProject) {
            return true;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    public SelectionType getUpstream() {
        return upstream;
    }

    public void setUpstream(SelectionType upstream) {
        this.upstream = upstream;
    }

    @Override
    public Environment setUp(final AbstractBuild build, Launcher launcher, final BuildListener listener) throws IOException, InterruptedException {
        return new BuildWrapper.Environment() {

            @Override
            public void buildEnvVars(Map<String, String> env) {
                super.buildEnvVars(env);    //To change body of overridden methods use File | Settings | File Templates.

                try {
                    URL url = new URL(Jenkins.getInstance().getRootUrl());

                    url = new URL(url, "plugin/repository/project/");

                    if (upstream instanceof SelectionTypeUpstream ) {
                        // What is the upstream project name?
                        Cause.UpstreamCause theCause = (Cause.UpstreamCause)build.getCause(Cause.UpstreamCause.class);
                        String theProject;
                        String theBuild;
                        if (theCause == null) {
                            ParametersAction action = build.getAction(ParametersAction.class);
                            if( action == null )
                            {
                                listener.getLogger().print("You asked for an upstream repository, but it does not exist");
                                return;
                            }
                            RunParameterValue value = (RunParameterValue) action.getParameter("Upstream");

                            theProject = value.getJobName();
                            theBuild   = value.getNumber();
                        } else {
                            theProject = theCause.getUpstreamProject();
                            theBuild = "" + theCause.getUpstreamBuild();
                        }
                        url = new URL(url, theProject);
                    } else if(upstream instanceof SelectionTypeSpecified) {
                        url = new URL(url, ((SelectionTypeSpecified)upstream).path);
                    } else {
                        // Specific
                        url = new URL(url, ((SelectionTypeProject)upstream).project);
                    }

                    url = addBuildId(url, upstream);

                    env.put("Jenkins.Repository", url.toExternalForm());
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }

            private URL addBuildId(URL url, SelectionType upstream) throws MalformedURLException {
                String buildId = null;
                if( upstream instanceof SelectionTypeUpstream)
                    buildId = ((SelectionTypeUpstream)upstream).buildId;
                else if( upstream instanceof SelectionTypeProject)
                    buildId = ((SelectionTypeProject)upstream).buildId;

                if( buildId == null )
                    return url;

                if ( "repository".equalsIgnoreCase("repository"))
                        url = new URL(url, "LastSuccessful/repository/");
                    else
                        url = new URL(url, "LastSuccessful/repositoryChain/");

                return url;
            }

        };
    }


    public String getIconFileName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getDisplayName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getUrlName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}