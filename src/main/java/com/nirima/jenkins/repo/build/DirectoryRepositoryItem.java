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

import com.nirima.jenkins.RepositoryPlugin;
import com.nirima.jenkins.repo.AbstractRepositoryElement;
import com.nirima.jenkins.repo.RepositoryDirectory;
import com.nirima.jenkins.repo.RepositoryElement;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Represent a directory
 */
public class DirectoryRepositoryItem extends AbstractRepositoryElement<String> implements RepositoryDirectory {

    private static final Logger LOGGER = Logger.getLogger(RepositoryPlugin.class.getName());

    Map<String, RepositoryElement> items = new HashMap<String, RepositoryElement>();

    protected DirectoryRepositoryItem(RepositoryElement parent, String item) {
        super(parent, item);
    }

    @Override
    public String getName() {
        return item;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Collection<? extends RepositoryElement> getChildren() {
        return items.values();  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void insert(File f, String path)
    {
        if( path.contains("/") )
        {
            int idx = path.indexOf("/");
            String dir = path.substring(0, idx);
            String rest = path.substring(idx+1);

            RepositoryElement dirElement = getChild(dir); // Get directory element
            if( dirElement == null )
            {
                // It doesn't already exist so create it:
                dirElement = add(new DirectoryRepositoryItem(this, dir));
            }

            // Insert into that directory.
            ((DirectoryRepositoryItem)dirElement).insert(f, rest);
        }
        else
        {
            // Not a path but a file for this location
            add(new FileRepositoryItem(this,f,path));
        }
    }

    protected RepositoryElement add(RepositoryElement dirElement)
    {
        if ( items.containsKey(dirElement.getName()) )
        {
            LOGGER.warning("Already have element named " + dirElement.getName() + " for path " + getPath());
        }
        items.put(dirElement.getName(), dirElement);

        return dirElement;
    }

    public RepositoryElement getChild(String element)
    {
       return items.get(element);
    }
}
