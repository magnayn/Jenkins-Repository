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
package com.nirima.jenkins.repo.fs;

import com.nirima.jenkins.repo.AbstractRepositoryElement;
import com.nirima.jenkins.repo.RepositoryContent;
import com.nirima.jenkins.repo.RepositoryDirectory;
import com.nirima.jenkins.repo.RepositoryElement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;

public class FileRepositoryItem extends AbstractRepositoryElement implements RepositoryContent {
    protected File item;

    public FileRepositoryItem(RepositoryDirectory parent, File item) {
        super(parent);
        this.item = item;
    }

    @Override
    public String getName() {
        return item.getName();  //To change body of implemented methods use File | Settings | File Templates.
    }

    public InputStream getContent() throws FileNotFoundException {
        return new FileInputStream(item);
    }

    public Date getLastModified() {
        return new Date(item.lastModified());
    }

    public Long getSize() {
        return item.length();
    }

    public String getDescription() {
        return "";
    }

    public String getContentType()
    {
        return null;
    }
}
