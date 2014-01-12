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

/**
 * @author nigelm
 */
public class Unlock extends MethodBase {
    /*
     * (non-Javadoc)
     * 
     * @see nrm.webdav.interfaces.IMethod#invoke()
     */
    @Override
    public void invoke(IDavContext ctxt) throws MethodException {
        String lockToken = getLockToken();
        if (lockToken == null) {
            this.getResponse().setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        IDavItem item = getRepo().getItem(getDavContext(), this.getPath());

        if (item == null) {
            this.getResponse().setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        IDavItem file = item;
        if (!file.isLocked(ctxt)) {
            this.getResponse().setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
            return;
        }

        IDavLock token = file.getLock(ctxt, lockToken);
        if (token == null) {
            this.getResponse().setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
            return;
        }

        token.unlock(ctxt);

    }

}
