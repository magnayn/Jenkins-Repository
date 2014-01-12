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
package com.nirima.jenkins.repo.build;

import com.google.common.collect.Lists;
import com.nirima.jenkins.repo.util.DirectoryPopulatorVisitor;
import com.nirima.jenkins.repo.util.HudsonWalker;
import com.nirima.jenkins.repo.util.IDirectoryPopulator;
import hudson.maven.MavenBuild;
import hudson.maven.MavenModule;
import hudson.maven.reporters.MavenArtifact;
import hudson.maven.reporters.MavenArtifactRecord;
import hudson.model.Run;
import com.nirima.jenkins.repo.AbstractRepositoryDirectory;
import com.nirima.jenkins.repo.RepositoryDirectory;
import com.nirima.jenkins.repo.RepositoryElement;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import hudson.maven.MavenModuleSetBuild;


public class ProjectBuildRepositoryRoot extends AbstractRepositoryDirectory implements RepositoryDirectory {
    private final String name;

    protected final Run item;

    public ProjectBuildRepositoryRoot(RepositoryDirectory parent, final Run item, String name) {
        super(parent);
        if( item == null || name == null )
            throw new IllegalArgumentException("Must specify run and name");
        this.name = name;
        this.item = item;
    }

    @Override
    public String getName() {
        return name;
    }

    public Collection<? extends RepositoryElement> getChildren() {
       return Lists.newArrayList(
            new SimpleOnDemandItem(this,"repository", new IDirectoryPopulator() {
                public void populate(DirectoryRepositoryItem directory) {
                    HudsonWalker.traverse(new DirectoryPopulatorVisitor(directory,false) ,item);
                }
            }),
            new SimpleOnDemandItem(this,"repositoryChain", new IDirectoryPopulator() {
                public void populate(DirectoryRepositoryItem directory) {
                    HudsonWalker.traverseChain(new DirectoryPopulatorVisitor(directory,false) ,item);
                }
            })
       );
    }

    public String getDescription() {
        return item.toString();
    }


    @Override
    public String toString() {
        return "ProjectBuildRepositoryRoot{" + name + "," + item.toString() + "}";
    }

}
