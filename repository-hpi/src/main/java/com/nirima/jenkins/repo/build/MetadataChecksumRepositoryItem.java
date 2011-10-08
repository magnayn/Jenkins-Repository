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

import hudson.maven.MavenBuild;
import hudson.maven.reporters.MavenArtifact;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * Represent a {@code maven-metadata.xml} file.
 */
public class MetadataChecksumRepositoryItem extends TextRepositoryItem {

    private String algorithm;
    private MetadataRepositoryItem item;

    public MetadataChecksumRepositoryItem(String algorithm, MetadataRepositoryItem item) {
        this.algorithm = algorithm;
        this.item = item;
    }

    public String getName() {
        return "maven-metadata.xml." + algorithm.toLowerCase();
    }

    public Date getLastModified() {
        return item.getLastModified();
    }

    public String getDescription() {
        return item.getDescription();
    }

    protected String generateContent() {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm.toUpperCase());
            byte[] digest = md.digest(item.generateContent().getBytes());
            String hex = new BigInteger(1, digest).toString(16);
            return (hex.length() % 2 == 0) ? hex : ("0" + hex);
        } catch (NoSuchAlgorithmException nsae) {
            return "";
        }
    }
}
