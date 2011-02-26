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


public class ProjectBuildRepositoryRoot extends AbstractRepositoryDirectory<Run> implements RepositoryDirectory {
    private String name;

    protected DirectoryRepositoryItem root;

    public ProjectBuildRepositoryRoot(RepositoryElement parent, Run item, String name) {
        super(parent, item);
        this.name = name;
        root = new DirectoryRepositoryItem(this,"repository");
    }

    @Override
    public String getName() {
        return name;
    }

    public Collection<? extends RepositoryElement> getChildren() {

        if( item instanceof MavenModuleSetBuild )
        {
            Map<MavenModule,List<MavenBuild>> modulesMap = ((MavenModuleSetBuild) item).getModuleBuilds();

            for(List<MavenBuild> builds : modulesMap.values())
            {
                for( MavenBuild build : builds)
                {
                    MavenArtifactRecord artifacts = build.getAction(MavenArtifactRecord.class);

                    register(build, artifacts.pomArtifact);
                    register(build, artifacts.mainArtifact);
                    for(MavenArtifact art : artifacts.attachedArtifacts)
                    {
                        register(build, art);
                    }
                }

            }

            return Lists.newArrayList(root);
        }

        // TODO: Don't know if Ant/freestyle builds could be examined for artifacts?

        return new ArrayList();
    }

    private void register(MavenBuild build, MavenArtifact mavenArtifact) {
        String path = mavenArtifact.groupId.replace('.','/') + "/" + mavenArtifact.artifactId + '/' + mavenArtifact.version + "/" + mavenArtifact.canonicalName;
        File f = new File(new File(new File(new File(build.getArtifactsDir(), mavenArtifact.groupId), mavenArtifact.artifactId), mavenArtifact.version), mavenArtifact.fileName);
        if( f.exists() )
        {
           insertFile(f, path);
        }

    }

    private void insertFile(File f, String path) {
        System.out.println("" + path + " = " + f.getAbsolutePath());
        root.insert(f, path);
    }
}
