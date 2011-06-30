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

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;


public class UrlRepositoryItem extends AbstractRepositoryElement implements RepositoryContent {
    protected String name;
    protected URL item;

    public UrlRepositoryItem(RepositoryDirectory parent, URL item, String name) {
        super(parent);
        this.name = name;
        this.item = item;
    }

    @Override
    public String getName() {
        return name;
    }

    public InputStream getContent() throws IOException {
        return item.openStream();
    }

    public Date getLastModified() {

        try
        {
            File f = new File(item.getFile());
            return new Date(f.lastModified());
        }
        catch(Exception ex)
        {
            return null;
        }

    }

    public Long getSize() {
         try
        {
           File f = new File(item.getFile());
           return f.length();
        }
        catch(Exception ex)
        {
            return null;
        }
    }

    public String getDescription() {
        return "";
    }

    public String getContentType()
    {
        return null;
    }
}
