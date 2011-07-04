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
package com.nirima.jenkins;

import hudson.model.AbstractBuild;
import hudson.model.Describable;
import hudson.model.Descriptor;
import jenkins.model.Jenkins;
import org.apache.commons.httpclient.Cookie;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;


public abstract class SelectionType implements Describable<SelectionType>, Serializable {

    public class RepositoryDoesNotExistException extends Exception {
    }


    public Descriptor getDescriptor() {
        return Jenkins.getInstance().getDescriptorOrDie(getClass());
    }

    protected URL addBuildId(URL url, String buildId) throws MalformedURLException {

        if (buildId == null)
            return url;

        if ("repository".equalsIgnoreCase("repository"))
            url = new URL(url, "LastSuccessful/repository/");
        else
            url = new URL(url, "LastSuccessful/repositoryChain/");

        return url;
    }

    public abstract URL getUrl(AbstractBuild build) throws MalformedURLException, RepositoryDoesNotExistException;
}
