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
package com.nirima.jenkins.webdav.impl.methods;

import com.nirima.jenkins.webdav.interfaces.IDavContext;
import com.nirima.jenkins.webdav.interfaces.IDavItem;
import com.nirima.jenkins.webdav.interfaces.IDavRepo;
import com.nirima.jenkins.webdav.interfaces.MethodException;


import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;

/**
 * @author nigelm
 */
public class Move extends MethodBase {

    /*
     * (non-Javadoc)
     * 
     * @see nrm.webdav.interfaces.IMethod#invoke()
     */
    @Override
    public void invoke(IDavContext ctxt) throws MethodException {
        try {
            IDavRepo repo = getRepo();

            IDavItem sourceItem = repo.getItem(getDavContext(), this.getPath());

            if (sourceItem != null) {
                String destination = URLDecoder.decode(this.getRequest().getHeader("Destination"), "UTF-8");
                // Can only move a file within this repository
                if (destination.startsWith(this.getBaseUrl())) {
                    // Remove the prefix (leaving leading slash)
                    destination = destination.substring(this.getBaseUrl().length());
                    // Check if there is already another file at this destination
                    IDavItem destinationItem = null;
                    try {
                        destinationItem = repo.getItem(getDavContext(), destination);
                    } catch (Exception e) {
                        // Item does not exist
                    }

                    if (destinationItem != null) {
                        // Don't allow moving items over ones that already exist
                        this.getResponse().setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
                        return;
                    } else {
                        int lastSlash = destination.lastIndexOf("/");
                        if (lastSlash != -1) {
                            String newFilename = destination.substring(lastSlash + 1);
                            String destParentPath = "";
                            if (lastSlash > 0) {
                                destParentPath = destination.substring(0, lastSlash);
                            }
                            if (destParentPath.equalsIgnoreCase(sourceItem.getParentPath(ctxt))) {
                                // Simple file rename with no conflicts
                                sourceItem.rename(ctxt, newFilename);
                                this.getResponse().setStatus(HttpServletResponse.SC_CREATED);
                                return;
                            }
                        }
                    }
                }
            }

            this.getResponse().setStatus(HttpServletResponse.SC_CONFLICT);

        } catch (Exception e) {
            throw new MethodException("Error putting object", e);
        }

    }

}
