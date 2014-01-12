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
package com.nirima.jenkins.repository;

import com.nirima.jenkins.repo.AbstractRepositoryDirectory;
import com.nirima.jenkins.repo.RepositoryDirectory;
import com.nirima.jenkins.repo.RepositoryElement;
import com.nirima.jenkins.repo.RootElement;
import com.nirima.jenkins.repo.build.DirectoryRepositoryItem;
import com.nirima.jenkins.repo.fs.FileDirectoryRepositoryItem;
import com.nirima.jenkins.repo.fs.UrlRepositoryItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class ToolRepositoryRoot extends AbstractRepositoryDirectory implements RepositoryDirectory{
    public ToolRepositoryRoot(ToolsRepository toolsRepository, RootElement parent) {
        super(parent);

    }

    @Override
    public Collection<? extends RepositoryElement> getChildren() {
        List<RepositoryElement> elements = new ArrayList<RepositoryElement>();

        File file = new File(this.getClass().getResource("/tools").getFile());
        File[] listFiles = file.listFiles();
        if( listFiles != null) {
            for( File f : listFiles )
            {
               RepositoryElement e1 =  new FileDirectoryRepositoryItem(null, f);

               elements.add( e1 );
            }
        }

        return elements;
    }

    @Override
    public String getName() {
        return "tools";
    }

    @Override
    public String toString() {
        return "ToolRepositoryRoot{}";
    }
}
