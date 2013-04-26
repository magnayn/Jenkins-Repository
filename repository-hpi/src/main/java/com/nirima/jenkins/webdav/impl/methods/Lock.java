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

import com.nirima.jenkins.webdav.impl.DAVItemSerializer;
import com.nirima.jenkins.webdav.interfaces.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamWriter;

/**
 * @author nigelm TODO To change the template for this generated type comment go to Window - Preferences - Java - Code
 *         Style - Code Templates
 */
public class Lock extends MethodBase {
    private static Logger s_logger = LoggerFactory.getLogger(Lock.class);

    /*
     * (non-Javadoc)
     * 
     * @see nrm.webdav.interfaces.IMethod#invoke()
     */
    @Override
    public void invoke(IDavContext ctxt) throws MethodException {
        String lockToken = parseIfHeader();
        int timeout = getHeaderTime("Timeout", -1);
        boolean created = false;

        if (!getRepo().supportsLocking()) {
            this.getResponse().setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
            // m_response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        IDavItem item = getRepo().getItem(getDavContext(), this.getPath());

        if (item == null) {
            // IF the item doesn't exist... we're supposed to create it.
            // 
            IDavRepo repo = getRepo();
            int lastSlash = this.getPath().lastIndexOf("/");
            String parent = this.getPath().substring(0, lastSlash);
            IDavCollection parentFolder = (IDavCollection) repo.getItem(getDavContext(), parent);
            item = parentFolder.createItem(getDavContext(), this.getPath().substring(lastSlash + 1));
            created = true;
        }

        IDavLock lockItem;
        s_logger.info("Locking request for " + timeout + "secs");

        if (lockToken != null) {
            lockItem = item.getLock(ctxt, lockToken);
            lockItem.renew(timeout);
        } else {
            lockItem = item.lock(ctxt, timeout);
        }

        // m_response.setContentType("text/xml; charset=UTF-8");
        // Output the response

        XMLStreamWriter response = this.createXmlResponse();
        DAVItemSerializer dis = new DAVItemSerializer();
        try {
            //response.writeStartDocument("UTF-8");
            response.setPrefix("a", "DAV:");
            response.writeStartElement("DAV:", "prop");
            dis.generateLockXml(response, lockItem);
            response.writeEndElement();
            response.writeEndDocument();
            response.flush();
            response.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new MethodException("Error generating lock XML", e);
        }

        if (created) this.getResponse().setStatus(HttpServletResponse.SC_CREATED);

    }

}
