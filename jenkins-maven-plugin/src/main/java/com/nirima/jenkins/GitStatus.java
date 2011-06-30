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

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.IndexDiff;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.FileTreeIterator;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class GitStatus {

    public static class Status {
        IndexDiff index;
        ObjectId sha1;
        FileRepository repository;

        public Status(ObjectId id, FileRepository repository) throws IOException {
            sha1 = id;
            this.repository = repository;

            FileTreeIterator workingTreeIt = new FileTreeIterator(repository);
            index = new IndexDiff(repository, Constants.HEAD, workingTreeIt);
            index.diff();
        }

        public boolean isDirty(File directory) {
            return (match(directory, index.getAdded()) ||
                    match(directory, index.getChanged()) ||
                    match(directory, index.getMissing()) ||
                    match(directory, index.getModified()) ||
                    match(directory, index.getRemoved()));

        }

        private boolean match(File directory, Set<String> items) {
            File workTree = repository.getWorkTree();

            String substring = directory.getPath().substring( workTree.getPath().length());
            if( substring.length() > 0)
                substring = substring.substring(1);

            for(String item : items)
            {
                if( item.startsWith(substring) )
                    return true;
            }
            return false;
        }
    }

    private Map<ObjectId, Status> fetched = new HashMap<ObjectId, Status>();

    public Status getStatus(File sourceDirectory) throws IOException {

        final FileRepository repository = new FileRepositoryBuilder()
                .readEnvironment()
                .findGitDir(sourceDirectory)
                .build();

        ObjectId head = repository.resolve("HEAD");

        if (fetched.containsKey(head))
            return fetched.get(head);


        Status status = new Status(head, repository);
        fetched.put(head, status);
        return status;
    }
}
