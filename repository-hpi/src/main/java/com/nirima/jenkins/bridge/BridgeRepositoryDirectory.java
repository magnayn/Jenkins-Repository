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
import com.nirima.jenkins.repo.RepositoryDirectory;
import com.nirima.jenkins.repo.RepositoryElement;
import com.nirima.jenkins.webdav.interfaces.*;

import java.util.ArrayList;
import java.util.Date;


public class BridgeRepositoryDirectory extends BridgeRepositoryElement<RepositoryDirectory> implements IDavCollection {

    public BridgeRepositoryDirectory(IDavRepo repo, RepositoryDirectory repositoryContent) {
        super(repo, repositoryContent);
    }

    public IDavFile createItem(IDavContext dctx, String name) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public IDavCollection createCollection(IDavContext dctx, String name) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ArrayList<IDavItem> getChildren(IDavContext ctxt) {
        ArrayList<IDavItem> items = new ArrayList<IDavItem>();

        for(RepositoryElement e : element.getChildren())
        {
            if( e instanceof RepositoryContent )
                items.add( new BridgeRepositoryContent(this.repo, (RepositoryContent)e ));
            else
                items.add( new BridgeRepositoryDirectory(this.repo, (RepositoryDirectory)e ));
        }

        return items;
    }

    public Date getLastModified() {
        return new Date();
    }

    public Date getCreationDate() {
        return new Date();
    }
}
