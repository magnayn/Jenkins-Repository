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
package com.nirima.jenkins.repo;

import com.google.common.collect.Lists;
import com.nirima.jenkins.repo.project.ProjectBuildList;
import com.nirima.jenkins.repo.project.ProjectsElement;
import com.nirima.jenkins.repo.virtual.AllSHA1RepositoryRoot;
import com.nirima.jenkins.repo.virtual.VirtualRepositoryRoot;
import hudson.model.BuildableItemWithBuildWrappers;
import org.apache.maven.model.Repository;

import java.util.ArrayList;
import java.util.Collection;


public class RootElement extends AbstractRepositoryDirectory implements RepositoryDirectory {

    public RootElement() {
        super(null);
    }

    public Collection<? extends RepositoryElement> getChildren() {

        Collection<RepositoryElement> children = new ArrayList<RepositoryElement>();

        // Default set
        children.add( new ProjectsElement(this) );
        children.add( new VirtualRepositoryRoot(this) );
        children.add( new AllSHA1RepositoryRoot(this) );

        for(RepositoryExtensionPoint r : RepositoryExtensionPoint.all()) {
            children.add( r.getRepositoryRoot(this) );
        }

        return children;
    }

    public String getName() {
        return null;
    }
    @Override
    public String getPath() {
        return "";
    }

    @Override
    public String toString() {
        return "RootElement{}";
    }

}
