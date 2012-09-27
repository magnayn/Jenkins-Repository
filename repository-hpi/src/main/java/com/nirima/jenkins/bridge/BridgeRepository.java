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
import com.nirima.jenkins.repo.RootElement;
import com.nirima.jenkins.webdav.interfaces.*;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;


public class BridgeRepository implements IDavRepo {
    private static final Logger log = Logger.getLogger(BridgeRepository.class);

    IMimeTypeResolver mimeTypeResolver;
    RepositoryDirectory rootElement;

    public BridgeRepository(IMimeTypeResolver mimeTypeResolver)
    {
        this(new RootElement(),mimeTypeResolver);
    }

    public BridgeRepository(RepositoryDirectory root, IMimeTypeResolver mimeTypeResolver)
    {
        this.rootElement = root;
        this.mimeTypeResolver = mimeTypeResolver;
    }

    public IDavItem getItem(IDavContext ctxt, String path) {

        try
        {
            RepositoryElement currentItem = rootElement;


            // Split into sections
            String[] pathElements = path.substring(1).split("/");


            // Ignore breakdown case if '/'
            if (pathElements.length > 1 || pathElements[0].length() > 0) {
                for (String element : pathElements) {
                    if (currentItem instanceof RepositoryDirectory) {
                        RepositoryDirectory currentDirectory = (RepositoryDirectory) currentItem;
                        currentItem = currentDirectory.getChild(element);
                    }

                }
            }

            return bridge(currentItem);
        }
        catch(Exception ex)
        {
            log.error("No such repository path " + path);
            return null;
        }



    }

    protected IDavItem bridge(RepositoryElement item) {
        if( item instanceof RepositoryContent)
            return new BridgeRepositoryContent(this, (RepositoryContent)item);
        else
            return new BridgeRepositoryDirectory(this, (RepositoryDirectory)item);
    }

    public Collection<IDavItem> getItems(IDavContext ctxt, IDavItem item, int depth) {
        ArrayList<IDavItem> items = new ArrayList<IDavItem>();
        items.add(item);

        if (item instanceof IDavCollection)
        {
            addChildItems(ctxt, items, (IDavCollection)item, depth);
        }

        return items;
    }

      private static void addChildItems(IDavContext ctxt, ArrayList<IDavItem> collection, IDavCollection dirItem, int depth)
    {
        if (depth == 0) return;
        ArrayList<IDavItem> childItems = dirItem.getChildren(ctxt);
        collection.addAll(childItems);
        for (IDavItem item : childItems)
        {
            if (item instanceof IDavCollection) addChildItems(ctxt, collection, (IDavCollection)item, depth - 1);
        }
    }

    public IDavItem getRepositoryRoot(IDavContext ctxt) {
        return new BridgeRepositoryDirectory(this, rootElement);
    }

    public boolean supportsLocking() {
        return false;
    }

    public IMimeTypeResolver getMimeTypeResolver() {
        return mimeTypeResolver;
    }

    public void setMimeTypeResolver(IMimeTypeResolver resolver) {
        this.mimeTypeResolver = resolver;
    }
}
