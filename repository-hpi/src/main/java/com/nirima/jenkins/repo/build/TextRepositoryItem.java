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

import com.nirima.jenkins.repo.RepositoryContent;
import com.nirima.jenkins.repo.RepositoryDirectory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * A repository item that generates text on the fly.
 */
public abstract class TextRepositoryItem implements RepositoryContent {

    private RepositoryDirectory directory;

    public RepositoryDirectory getParent() {
        return directory;
    }

    public void setParent(RepositoryDirectory parent)
    {
        this.directory = parent;
    }

    public String getPath() {
        return directory.getPath() + "/" + getName();
    }

    public InputStream getContent() throws Exception {
        return new ByteArrayInputStream(generateContent().getBytes());
    }

    public Long getSize() {
        return (long)generateContent().length();
    }

    public String getContentType()
    {
        return "text/plain";
    }

    protected abstract String generateContent();
}
