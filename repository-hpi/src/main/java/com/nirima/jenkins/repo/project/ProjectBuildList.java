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

package com.nirima.jenkins.repo.project;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import hudson.model.BuildableItemWithBuildWrappers;
import hudson.model.Result;
import hudson.model.Run;
import hudson.plugins.git.util.BuildData;
import com.nirima.jenkins.repo.AbstractRepositoryDirectory;
import com.nirima.jenkins.repo.RepositoryDirectory;
import com.nirima.jenkins.repo.RepositoryElement;
import com.nirima.jenkins.repo.build.ProjectBuildRepositoryRoot;

import java.util.*;

public class ProjectBuildList extends AbstractRepositoryDirectory implements RepositoryDirectory {

    public enum Type {
        SHA1,
        Build
    }

    Type type;
     BuildableItemWithBuildWrappers item;

    protected ProjectBuildList(RepositoryDirectory parent, BuildableItemWithBuildWrappers item, Type type) {
        super(parent);
        this.type = type;
        this.item = item;
    }

    @Override
    public String getName() {
        return type.name();  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Collection<ProjectBuildRepositoryRoot> getChildren() {


        if (type == Type.Build) {
            Function<Run, ProjectBuildRepositoryRoot> fn;

            fn = new Function<Run, ProjectBuildRepositoryRoot>() {

                public ProjectBuildRepositoryRoot apply(Run r) {
                    if( r.getResult() != Result.SUCCESS )
                        return null;
                    return new ProjectBuildRepositoryRoot(ProjectBuildList.this, r, "" + r.getNumber());
                }
            };

            // Transform builds into items
            Iterable<ProjectBuildRepositoryRoot> i = Iterables.transform(item.asProject().getBuilds(), fn);

            // Remove NULL entries
            return Lists.newArrayList(Iterables.filter(i, new Predicate<ProjectBuildRepositoryRoot>() {
                public boolean apply(ProjectBuildRepositoryRoot projectBuildRepositoryRoot) {
                    return projectBuildRepositoryRoot != null;
                }
            }));

        } else {

            Map<String, ProjectBuildRepositoryRoot> children = new HashMap<String, ProjectBuildRepositoryRoot>();

            for (Run run : item.asProject().getBuilds()) {
                BuildData bd = run.getAction(BuildData.class);
                if (bd != null && run.getResult() == Result.SUCCESS) {
                    String sha1 = bd.getLastBuiltRevision().getSha1String();

                    // Most recent, only if successful
                    if (!children.containsKey(sha1) && run.getResult().isBetterOrEqualTo(Result.SUCCESS)) {
                        children.put(sha1, new ProjectBuildRepositoryRoot(this, run, sha1));
                    }
                }
            }

            return children.values();


        }


    }


}
