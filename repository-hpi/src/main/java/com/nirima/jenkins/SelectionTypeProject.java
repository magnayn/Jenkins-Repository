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

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.nirima.jenkins.action.ProjectRepositoryAction;
import com.nirima.jenkins.action.RepositoryAction;
import hudson.Extension;
import hudson.model.*;
import hudson.tasks.BuildWrapper;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.DataBoundConstructor;

import java.net.MalformedURLException;
import java.util.List;
import hudson.plugins.promoted_builds.PromotedBuildAction;

public class SelectionTypeProject extends SelectionType {
    public String project;
    public String build;
    public String promoted;

    @DataBoundConstructor
    public SelectionTypeProject(String project, String build, String promoted) {
        this.project = project;
        this.build = build;
        this.promoted = promoted;
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

    public String getPromoted() {
        return promoted;
    }

    public void setPromoted(String promoted) {
        this.promoted = promoted;
    }

    @Override
    public RepositoryAction getAction(AbstractBuild theBuild) throws MalformedURLException, RepositoryDoesNotExistException {

        int id;
        String suffix;

        if( build.equalsIgnoreCase("promotedRepository"))
        {
            suffix = "repository";
            id = getPromotedBuildNumber(project, promoted);
        }
        else if(build.equalsIgnoreCase("promotedRepositoryChain"))
        {
            suffix = "repositoryChain";
            id = getPromotedBuildNumber(project, promoted);
        }
        else
        {
            suffix = build;
            id = getLastSuccessfulBuildNumber(project);
        }

        return new ProjectRepositoryAction(project, id, suffix);
    }

    private BuildableItemWithBuildWrappers getProject(final String project) {
        BuildableItemWithBuildWrappers item = Iterables.find(
                Jenkins.getInstance().getAllItems(BuildableItemWithBuildWrappers.class),
                new Predicate<BuildableItemWithBuildWrappers>() {
                    public boolean apply(BuildableItemWithBuildWrappers buildableItemWithBuildWrappers) {
                        return buildableItemWithBuildWrappers.getName().equals(project);
                    }
                });
        return item;
    }

    private int getLastSuccessfulBuildNumber(final String project) {
        BuildableItemWithBuildWrappers item = getProject(project);

        return item.asProject().getLastSuccessfulBuild().getNumber();
    }

    private int getPromotedBuildNumber(final String project, final String promoted) {
        BuildableItemWithBuildWrappers item = getProject(project);


        Iterable<AbstractBuild> promotedItems = Iterables.filter(item.asProject().getBuilds(), new Predicate() {
            public boolean apply(Object o) {
                AbstractBuild abstractBuild = (AbstractBuild)o;

                PromotedBuildAction pba = abstractBuild.getAction(PromotedBuildAction.class);
                return ( pba != null && pba.getPromotion(promoted) != null );

            }
        });


        Ordering<AbstractBuild> ordering = new Ordering<AbstractBuild>() {
            @Override
            public int compare(AbstractBuild l,  AbstractBuild r) {
                return r.getNumber() - l.getNumber();
            }
        };

        try
        {
            return ordering.max(promotedItems).getNumber();
        }
        catch(Exception ex)
        {
            throw new RuntimeException("No promotion of type " + promoted + " in project " + project);
        }
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
