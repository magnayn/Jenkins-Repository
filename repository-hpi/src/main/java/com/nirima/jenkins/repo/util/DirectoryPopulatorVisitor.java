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
package com.nirima.jenkins.repo.util;

import com.nirima.jenkins.repo.RepositoryContent;
import com.nirima.jenkins.repo.build.ArtifactRepositoryItem;
import com.nirima.jenkins.repo.build.DirectoryRepositoryItem;
import com.nirima.jenkins.repo.build.MetadataChecksumRepositoryItem;
import com.nirima.jenkins.repo.build.MetadataRepositoryItem;
import hudson.maven.MavenBuild;
import hudson.maven.MavenModule;
import hudson.maven.MavenModuleSetBuild;
import hudson.maven.reporters.MavenArtifact;
import hudson.maven.reporters.MavenArtifactRecord;
import hudson.model.Run;

import java.util.HashMap;
import java.util.Map;

/**
 *  When called back, insert found artifacts into a directory.
 */
public class DirectoryPopulatorVisitor extends HudsonVisitor {

    DirectoryRepositoryItem root;
    public boolean allowOverwrite;

    public DirectoryPopulatorVisitor(DirectoryRepositoryItem root, boolean allowOverwrite)
    {
        this.root = root;
        this.allowOverwrite = allowOverwrite;
    }

    public @Override void visitArtifact(MavenBuild build, MavenArtifact artifact)
    {
        // add a Maven 2 compatible artifact entry
        ArtifactRepositoryItem repositoryItem = new ArtifactRepositoryItem(build, artifact, false);
        if (!repositoryItem.fileExists()) {
            return; // skip this artifact, its file was purged
        }
        add(repositoryItem);

        // if this is a snapshot, also add a Maven 3 compatible artifact entry
        if (artifact.version.endsWith("-SNAPSHOT")) {
            ArtifactRepositoryItem item = new ArtifactRepositoryItem(build, artifact, true);
            add(item);
            // add metadata for this artifact version
            String key = artifact.groupId + ":" + artifact.artifactId + ":" + artifact.version;
            MetadataRepositoryItem meta = metadata.get(key);
            if (meta == null) {
                metadata.put(key, meta = new MetadataRepositoryItem(build, artifact));
                add(meta);
            }
            meta.addArtifact(artifact, item);
        }
    }

    private void add(MetadataRepositoryItem meta) {
        root.insert(meta, meta.getPath(), allowOverwrite);
        // add checksums for the item as well
        root.insert(new MetadataChecksumRepositoryItem("md5", meta),
                meta.getPath() + ".md5", allowOverwrite);
        root.insert(new MetadataChecksumRepositoryItem("sha1", meta),
                meta.getPath() + ".sha1", allowOverwrite);
    }

    private void add(ArtifactRepositoryItem repositoryItem)
    {
        root.insert(repositoryItem, repositoryItem.getArtifactPath(), allowOverwrite);
        root.insert(new MetadataChecksumRepositoryItem("md5", repositoryItem),
                repositoryItem.getArtifactPath() + ".md5", allowOverwrite);
        root.insert(new MetadataChecksumRepositoryItem("sha1", repositoryItem),
                repositoryItem.getArtifactPath() + ".sha1", allowOverwrite);
    }

    private Map<String,MetadataRepositoryItem> metadata =
        new HashMap<String,MetadataRepositoryItem>();
}
