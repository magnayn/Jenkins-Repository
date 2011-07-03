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


import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

/**
 * Update your local repository
 *
 * @goal update
 * @phase process-sources
 */
public class UpdateMojo
        extends AbstractMojo {

    /**
     * @parameter default-value="${localRepository}"
     * @required
     * @readonly
     */
    private ArtifactRepository localRepository;

    /**
     * Map that contains the layouts.
     *
     * @component role="org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout"
     */
    private Map repositoryLayouts;

    /**
     * Location of the file.
     *
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private File outputDirectory;

    /**
     * @parameter expression="${project.ciManagement.url}"
     */
    private URL jenkinsUrl;

    /**
     * Any Object to print out.
     *
     * @parameter expression="${jenkins.url}"
     */
    private String url;

    /**
     * @parameter expression="${basedir}"
     */
    private File sourceDirectory;

    /**
     * @parameter expression="${project.artifactId}
     */
    private String artifactId;

    /**
     * @parameter default-value="${project}"
     */
    private MavenProject project;

    public static String firstDirty = null;

    public static final GitStatus gitStatus = new GitStatus();

    private Log log;

    public void setLog(org.apache.maven.plugin.logging.Log log) {
        this.log = log;
    }

    public Log getLog() {
        return this.log;
    }

    public void execute()
            throws MojoExecutionException {
        try {

            if (url != null && url.length() > 0) {
                jenkinsUrl = new URL(url);
            }

            try {
                if (jenkinsUrl == null || jenkinsUrl.toURI().toString().length() == 0) {
                    throw new MojoExecutionException("You must specify a Jenkins URL in the ciManagement section of your POM.");
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
                throw new MojoExecutionException("Error in CI Settings");
            }

            GitStatus.Status status = gitStatus.getStatus(sourceDirectory);

            if (firstDirty == null && status.isDirty(sourceDirectory)) {
                log.warn("Project " + artifactId + " is dirty - rebuild from here.");
                firstDirty = artifactId;
            }

            if (!jenkinsUrl.toString().endsWith("/")) {
                jenkinsUrl = new URL(jenkinsUrl.toString() + "/");
            }

            URL url = new URL(jenkinsUrl, "plugin/repository/SHA1/" + status.sha1.name() + "/repository/");

            //System.out.println("Remote URL " + url.toExternalForm());

            File repo = new File(localRepository.getBasedir());

            IArtifactCopier ac = new SimpleArtifactCopier(url, repo);

            Artifact art = project.getArtifact();

            try {
                ac.updateAll(art);
            } catch (Exception ex) {
                throw new MojoExecutionException("Could not update to latest version from url " + url);
            }

        } catch (URISyntaxException e) {
            throw new MojoExecutionException("URL error", e);
        } catch (MalformedURLException e) {
            throw new MojoExecutionException("URL error", e);
        } catch (IOException e) {
            throw new MojoExecutionException("Error fetching data", e);
        }
    }
}
