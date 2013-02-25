package com.nirima.jenkins.action;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: magnayn
 * Date: 24/01/2013
 * Time: 16:35
 * To change this template use File | Settings | File Templates.
 */
public class PathInRepositoryAction extends RepositoryAction {

    URL url;

    public PathInRepositoryAction(String subPath) {

    }

    @Override
    public URL getUrl() throws MalformedURLException {
        return url;
    }

}
