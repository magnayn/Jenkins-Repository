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

    public @Override void visitArtifact(MavenBuild build, MavenArtifact mavenArtifact)
    {
        ArtifactRepositoryItem repositoryItem = new ArtifactRepositoryItem(build, mavenArtifact);
        root.insert(repositoryItem, repositoryItem.getArtifactPath(), allowOverwrite);

        // add this artifact to the artifactId directory metadata
        String dirKey = mavenArtifact.groupId + ":" + mavenArtifact.artifactId;
        MetadataRepositoryItem dirItem = metadata.get(dirKey);
        if (dirItem == null) {
            metadata.put(dirKey, dirItem = new MetadataRepositoryItem(build));
            String path = mavenArtifact.groupId.replace('.','/') + "/" +
                mavenArtifact.artifactId + "/maven-metadata.xml";
            addMetadataItem(dirItem, path);
        }
        dirItem.addArtifact(mavenArtifact, repositoryItem);

        // and also add a metadata item for this version of this artifact
        MetadataRepositoryItem item = new MetadataRepositoryItem(build);
        item.addArtifact(mavenArtifact, repositoryItem);
        String path = mavenArtifact.groupId.replace('.','/') + "/" + mavenArtifact.artifactId +
            "/" + mavenArtifact.version + "/maven-metadata.xml";
        addMetadataItem(item, path);
    }

    private void addMetadataItem(MetadataRepositoryItem item, String path) {
        root.insert(item, path, allowOverwrite);
        // add checksums for the item as well
        root.insert(new MetadataChecksumRepositoryItem("md5", item),
                    path + ".md5", allowOverwrite);
        root.insert(new MetadataChecksumRepositoryItem("sha1", item),
                    path + ".sha1", allowOverwrite);
    }

    private Map<String,MetadataRepositoryItem> metadata =
        new HashMap<String,MetadataRepositoryItem>();
}
