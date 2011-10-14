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
package com.nirima.jenkins.repo.util;

import com.google.common.collect.Lists;
import com.nirima.jenkins.repo.RepositoryElement;
import com.nirima.jenkins.repo.build.ArtifactRepositoryItem;
import hudson.maven.MavenBuild;
import hudson.maven.MavenModule;
import hudson.maven.MavenModuleSetBuild;
import hudson.maven.reporters.MavenArtifact;
import hudson.maven.reporters.MavenArtifactRecord;
import hudson.model.*;
import hudson.util.RunList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: magnayn
 * Date: 02/03/2011
 * Time: 16:19
 * To change this template use File | Settings | File Templates.
 */
public class HudsonWalker {

    /**
     * visit everything in order.
     *
     * @param visitor
     */
    public static void traverse(HudsonVisitor visitor) {
        for (BuildableItemWithBuildWrappers item : Hudson.getInstance().getAllItems(BuildableItemWithBuildWrappers.class)) {

            visitor.visitProject(item);

            List<? extends Run> runs = item.asProject().getBuilds();
            for (Run run : runs) {
                traverse(visitor, run);
            }
        }
    }

    /**
     * Visit projects and builds
     */
    public static void traverseProjectsAndBuilds(HudsonVisitor visitor ) {
        for (BuildableItemWithBuildWrappers item : Hudson.getInstance().getAllItems(BuildableItemWithBuildWrappers.class)) {

            visitor.visitProject(item);

            List<? extends Run> runs = item.asProject().getBuilds();
            for (Run run : runs) {
                 if (run instanceof MavenModuleSetBuild) {
                    MavenModuleSetBuild mmsb = (MavenModuleSetBuild) run;

                    visitor.visitModuleSet(mmsb);
                 }
            }
        }
    }


    /**
     * visit project chain, from current through parents.
     * @param visitor
     * @param run
     */
    public static void traverseChain(HudsonVisitor visitor, Run run)
    {
        if( run == null )
            return;

        traverse(visitor, run);
        Cause.UpstreamCause upstream = (Cause.UpstreamCause)run.getCause(Cause.UpstreamCause.class);
        if( upstream != null )
        {
            String project = upstream.getUpstreamProject();
            int build = upstream.getUpstreamBuild();

            AbstractProject item = (AbstractProject)Hudson.getInstance().getItem(project);
            Run r = (Run)item.getBuilds().get(build);

            traverseChain(visitor, r);
        }
    }

    /**
     * visit a run
     * @param visitor
     * @param run
     */
    public static void traverse(HudsonVisitor visitor, Run run) {
        if (run instanceof MavenModuleSetBuild) {
            MavenModuleSetBuild item = (MavenModuleSetBuild) run;

            visitor.visitModuleSet(item);

            Map<MavenModule, List<MavenBuild>> modulesMap = item.getModuleBuilds();

            for (List<MavenBuild> builds : modulesMap.values()) {
                for (MavenBuild build : builds) {

                    visitor.visitBuild(build);

                    MavenArtifactRecord artifacts = build.getAction(MavenArtifactRecord.class);
                    if( artifacts != null )
                    {
                        visitor.visitArtifact(build, artifacts.pomArtifact);

                        if (artifacts.mainArtifact != artifacts.pomArtifact) {
                            // Sometimes the POM is the only thing being made..
                            visitor.visitArtifact(build, artifacts.mainArtifact);
                        }
                        for (MavenArtifact art : artifacts.attachedArtifacts) {
                            visitor.visitArtifact(build, art);
                        }
                    }
                }

            }

        }
    }


}
