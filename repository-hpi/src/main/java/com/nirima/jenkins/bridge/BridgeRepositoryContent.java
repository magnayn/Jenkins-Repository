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
package com.nirima.jenkins.bridge;

import com.nirima.jenkins.repo.RepositoryContent;
import com.nirima.jenkins.webdav.interfaces.IDavFile;
import com.nirima.jenkins.webdav.interfaces.IDavRepo;

import java.io.InputStream;
import java.util.Date;


public class BridgeRepositoryContent extends BridgeRepositoryElement<RepositoryContent> implements IDavFile {


    public BridgeRepositoryContent(IDavRepo repo, RepositoryContent repositoryContent)
    {
        super(repo, repositoryContent);
    }

    public String getContentLanguage() {
        return "";  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getContentType() {
        String type =  element.getContentType();
        if( type == null )
        {
            type = repo.getMimeTypeResolver().getMimeType(getName());
        }
        // Last resort..
        if( type == null )
        {
            type = "text/plain";
        }
        return type;
    }

    public long getContentLength() {
        return element.getSize();
    }

    public String getETag() {
        return "";  //To change body of implemented methods use File | Settings | File Templates.
    }

    public InputStream getContent() {
        try {
            return element.getContent();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void putContent(String contentType, InputStream data) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Date getLastModified() {
        return element.getLastModified();
    }

    public Date getCreationDate() {
       return element.getLastModified();
    }
}
