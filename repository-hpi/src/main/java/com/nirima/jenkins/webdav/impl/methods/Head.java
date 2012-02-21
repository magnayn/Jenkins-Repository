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

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import com.nirima.jenkins.webdav.interfaces.IDavContext;
import com.nirima.jenkins.webdav.interfaces.IDavItem;
import com.nirima.jenkins.webdav.interfaces.IDavRepo;
import com.nirima.jenkins.webdav.interfaces.MethodException;

/**
 * @author nigelm
 */
public class Head extends MethodBase {

    private static Logger s_logger = Logger.getLogger(Head.class);

    /*
     * (non-Javadoc)
     * 
     * @see nrm.webdav.interfaces.IMethod#invoke()
     */
    @Override
    public void invoke(IDavContext ctxt) throws MethodException {
        try {
            if (suppliedHeader("Range")) s_logger.warn("Don't yet support RANGE");

            ArrayList<String> ifMatchTags = getETags("If-Match");
            ArrayList<String> ifNoneMatchTags = getETags("If-None-Match");

            Date ifModifiedSince = getHeaderDate("If-Modified-Since");
            Date ifUnmodifiedSince = getHeaderDate("If-Unmodified-Since");

            IDavItem item = getRepo().getItem(getDavContext(), this.getPath());

            if (item == null) {
                this.getResponse().setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            if (item instanceof IDavCollection) {
                if( !getPath().endsWith("/") )
                {
                    // Restate the proper location
                   this.getResponse().setHeader("Location", getUrl() + "/");
                   this.getResponse().setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
                   return;
                }

                this.getResponse().setHeader("Content-Type", "text/html");
                generateDirectoryListing(ctxt, (IDavCollection) item);

                return;
            }

            IDavFile fileItem = (IDavFile) item;
            // TODO: Check that we actually want to send this

            // if we get this far we can build the response
            this.getResponse().setHeader("ETag", "\"" + fileItem.getETag() + "\"");
            this.getResponse().setHeader("Last-Modified", new SimpleDateFormat(DAVItemSerializer.dateFormatString).format(fileItem.getLastModified()));

            String contentType = fileItem.getContentType();

            this.getResponse().setHeader("Content-Type", contentType);
            this.getResponse().setHeader("Content-Length", Long.toString(fileItem.getContentLength()));

            writeContent(fileItem);
        } catch (Exception e) {
            throw new MethodException("Exception", e);
        }
    }

    protected void writeContent(IDavFile fileItem) throws IOException {

    }

    protected void generateDirectoryListing(IDavContext ctxt, IDavCollection item) throws IOException {
        PrintWriter writer = this.getResponse().getWriter();
        String name = item.getName();
        if( name == null )
            name = "Repository";

        writer.write("<H1>" + name + "</H1>");
        writer.write("<TABLE>");
        for (IDavItem cItem : item.getChildren(ctxt)) {
            writer.write("<TR>");
            writer.write("<TD>" + cItem.getLastModified().toString() + "</TD>");

            if (cItem instanceof IDavFile) {
                IDavFile file = (IDavFile) cItem;

                writer.write("<TD>" + Long.toString(file.getContentLength()) + "</TD>");

                writer.write("<TD><A href='" +  file.getName() + "'>" + file.getName() + "</A></TD>");
                if (file.isLocked(ctxt))
                    writer.write("<TD>(Locked)</TD>");
                else
                    writer.write("<TD></TD>");
            } else {
                IDavCollection dir = (IDavCollection) cItem;


                writer.write("<TD>DIR</TD>");
                writer.write("<TD><A href='" + dir.getName() + "/'>" + dir.getName() + "</A></TD>");
                writer.write("<TD></TD>");
            }
            writer.write("</TR>");
        }
        writer.write("</TABLE>");
    }

}
