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

import com.nirima.jenkins.action.RepositoryAction;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.export.ExportedBean;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
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
                    RepositoryAction repositoryAction = upstream.getAction(build);
                    build.addAction(repositoryAction);
                    env.put("Jenkins.Repository", repositoryAction.getUrl().toExternalForm());
                    listener.getLogger().println("Setting environment Jenkins.Repository = " + repositoryAction.getUrl().toExternalForm());
                } catch (SelectionType.RepositoryDoesNotExistException x) {
                    listener.getLogger().println("You asked for an upstream repository, but it does not exist");
                    throw new RuntimeException(x);
                } catch (MalformedURLException e) {
                    listener.getLogger().println("Problem setting upstream repository URL");
                    throw new RuntimeException(e);
                }

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