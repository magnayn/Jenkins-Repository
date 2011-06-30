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

import com.nirima.jenkins.webdav.interfaces.*;


import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

/**
 * @author nigelm
 */
public class Put extends MethodBase {

    /*
     * (non-Javadoc)
     * 
     * @see nrm.webdav.interfaces.IMethod#invoke()
     */
    @Override
    public void invoke(IDavContext ctxt) throws MethodException {
        try {
            IDavRepo repo = getRepo();
            boolean created = false;
            String contentType = this.getRequest().getHeader("Content-Type");

            IDavItem item = repo.getItem(getDavContext(), this.getPath());
            IDavFile file = (IDavFile) item;

            if (item == null) {
                // The item couldn't be found, so it is a creation.
                // Make the item in the parent path.
                int lastSlash = this.getPath().lastIndexOf("/");
                String parent = this.getPath().substring(0, lastSlash);
                IDavCollection parentFolder = (IDavCollection) repo.getItem(getDavContext(), parent);
                file = parentFolder.createItem(getDavContext(), this.getPath().substring(lastSlash + 1));
                created = true;
            }

            InputStream putData = this.getRequest().getInputStream();
            file.putContent(contentType, putData);

            if (created)
                this.getResponse().setStatus(HttpServletResponse.SC_CREATED);
            else
                this.getResponse().setStatus(HttpServletResponse.SC_NO_CONTENT);

        } catch (Exception e) {
            throw new MethodException("Error putting object", e);
        }
    }

}
