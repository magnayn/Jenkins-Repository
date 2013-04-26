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

import com.nirima.jenkins.repo.RepositoryElement;
import com.nirima.jenkins.webdav.interfaces.IDavContext;
import com.nirima.jenkins.webdav.interfaces.IDavItem;
import com.nirima.jenkins.webdav.interfaces.IDavLock;
import com.nirima.jenkins.webdav.interfaces.IDavRepo;

import java.util.ArrayList;
import java.util.Date;


public abstract class BridgeRepositoryElement<T extends RepositoryElement> implements IDavItem {
    protected T element;
    protected final IDavRepo repo;

    public BridgeRepositoryElement(IDavRepo repo, T element)
    {
        if( element == null )
            throw new IllegalArgumentException();
        this.element = element;
        this.repo = repo;
    }

    public String getName() {
        return element.getName();
    }

    public String getPath(IDavContext ctxt) {
        return element.getPath();
    }

    public String getParentPath(IDavContext ctxt) {
        return element.getParent().getPath();
    }

    public IDavRepo getRepo() {
        return repo;
    }

    public void delete(IDavContext ctxt) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void rename(IDavContext ctxt, String newName) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isLocked(IDavContext ctxt) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public IDavLock lock(IDavContext ctxt, int timeout) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public IDavLock getLock(IDavContext ctxt, String token) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ArrayList<IDavLock> getLocks(IDavContext ctxt) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String toString() {
        return "Bridge element " + element.getPath();
    }
}
