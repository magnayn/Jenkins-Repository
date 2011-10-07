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
import hudson.maven.MavenBuild;
import hudson.maven.reporters.MavenArtifact;
import com.nirima.jenkins.repo.RepositoryDirectory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Represent a {@code maven-metadata.xml} file.
 */
public class MetadataRepositoryItem implements RepositoryContent {

    private long lastModified = 0L;
    private String groupId;
    private String artifactId;
    private Set<String> versions = new HashSet<String>();

    private RepositoryDirectory directory;
    private MavenBuild build;

    public MetadataRepositoryItem(MavenBuild build)
    {
        this.build    = build;
    }

    public void addArtifact(MavenArtifact artifact, ArtifactRepositoryItem item) {
        this.groupId = artifact.groupId;
        this.artifactId = artifact.artifactId;
        try {
            this.lastModified = Math.max(lastModified, item.getLastModified().getTime());
            this.versions.add(artifact.version);
        } catch (IllegalStateException ise) {
            // the artifact in question does not exist (it was probably pruned); ignore it
        }
    }

    public String getName() {
        return "maven-metadata.xml";
    }

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
        return new ByteArrayInputStream(formatMetadata().getBytes());
    }

    public Date getLastModified() {
        return new Date(lastModified);
    }

    public Long getSize() {
        return (long)formatMetadata().length();
    }

    public String getDescription() {
        return "From Build #" + build.getNumber() + " of " + build.getParentBuild().getParent().getName();
    }

    public String getContentType()
    {
        return "text/plain";
    }

    private String formatMetadata() {
        StringBuilder buf = new StringBuilder();
        buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        buf.append("<metadata>\n");
        buf.append("  <groupId>" + groupId + "</groupId>\n");
        buf.append("  <artifactId>" + artifactId + "</artifactId>\n");
        if (versions.size() == 1) {
            buf.append("  <version>" + versions.iterator().next() + "</version>\n");
        }
        buf.append("  <versioning>\n");
        buf.append("    <versions>\n");
        for (String version : versions) {
            buf.append("      <version>" + version + "</version>\n");
        }
        buf.append("    </versions>\n");
        buf.append("    <lastUpdated>" + _fmt.format(getLastModified()) + "</lastUpdated>\n");
        buf.append("  </versioning>\n");
        buf.append("</metadata>\n");
        return buf.toString();
    }

    protected static SimpleDateFormat _fmt = new SimpleDateFormat("yyyyMMddHHmmss");
}
