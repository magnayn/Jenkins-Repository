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
import com.nirima.jenkins.webdav.interfaces.IDavRepo;
import com.nirima.jenkins.webdav.interfaces.IMethod;
import com.nirima.jenkins.webdav.interfaces.MethodException;
import com.nirima.jenkins.xml.XmlSerializerException;
import com.nirima.jenkins.xml.XmlSerializerFactory;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * @author nigelm TODO To change the template for this generated type comment go to Window - Preferences - Java - Code
 *         Style - Code Templates
 */
public class MethodBase implements IMethod {
    private static Logger s_logger = LoggerFactory.getLogger(MethodBase.class);

    protected static String s_dateFormat = "EEE, dd MMM yyyy HH:mm:ss zzz";

    private HttpServletRequest m_request;
    private HttpServletResponse m_response;
    private String m_path;
    private String m_baseUrl;

    private IDavRepo m_repo;
    private IDavContext m_ctx;

    protected IDavContext getDavContext() {
        return m_ctx;
    }

    public void invoke() throws MethodException {
        invoke(m_ctx);
    }

    /*
     * (non-Javadoc)
     * 
     * @see nrm.webdav.interfaces.IMethod#invoke()
     */
    public void invoke(IDavContext ctxt) throws MethodException {
        s_logger.info("base:invoke Called");
    }

    protected IDavRepo getRepo() throws MethodException {
        return m_repo;
    }

    /*
     * (non-Javadoc)
     * 
     * @see nrm.webdav.interfaces.IMethod#init(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public void init(HttpServletRequest request, HttpServletResponse response, IDavContext ctx, IDavRepo repo, String root) {
        m_request = request;
        m_response = response;
        m_ctx = ctx;
        m_repo = repo;

        // TODO : what if it is https
        m_baseUrl = "http://" + request.getServerName();
        if (request.getServerPort() != 80) m_baseUrl += ":" + request.getServerPort();

        m_baseUrl += request.getContextPath() + request.getServletPath();

        // Path is the path into the repo we have requested

        // Root will be something like /config, so add to the base url
        m_baseUrl += root;

        // PathInfo will also be /config/woo, but we ignore the 1st part

        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            pathInfo = request.getServletPath();
        }

        m_path = request.getContextPath() + pathInfo;
        if (m_path == null) {
            m_path = "/";
        } else {
            m_path = m_path.substring(root.length());
        }

        s_logger.info(request.getMethod() + " " + m_baseUrl + " Called with path " + m_path);
    }

    protected XMLStreamWriter createXmlResponse() {
        try {
            //OutputStream os = new StreamLogger(m_response.getOutputStream());
            OutputStream os = m_response.getOutputStream();

            XMLStreamWriter serializer = XmlSerializerFactory.create(os);

            //serializer.setOutput(os, "UTF-8");
            return serializer;
        } catch (Exception e) {
            throw new RuntimeException("Problem creating xml response", e);
        }

    }

    protected XMLStreamReader getDocument() throws MethodException, XmlSerializerException, IOException {
        s_logger.info("Request length = " + m_request.getContentLength());
        if (m_request.getContentLength() == 0) {
            return null;
        }

        Reader r = m_request.getReader();
        /** Debugging
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        IOUtils.copy(r, baos);

        System.out.println("Content:" + new String(baos.toByteArray() ) + ":");

        ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );

        XMLStreamReader xpp = XmlSerializerFactory.createXMLStreamReader(bais);
        **/
        XMLStreamReader xpp = XmlSerializerFactory.createXMLStreamReader(r);
        return xpp;

    }

    protected boolean suppliedHeader(String name) {
        // Were we passed this header
        String header = m_request.getHeader(name);
        return (header != null && header.length() > 0);
    }

    protected String getHeader(String name) {
        return m_request.getHeader(name);
    }

    protected void addHeader(String name, String value) {
        m_response.addHeader(name, value);
    }

    protected int getHeaderInt(String name, int defaultValue) {
        String header = m_request.getHeader(name);
        try {
            if (header != null && header.length() > 0) return Integer.parseInt(header);
        } catch (Exception e) {
        }
        return defaultValue;
    }

    protected int getHeaderTime(String name, int defaultValue) {
        String header = m_request.getHeader(name);

        if (header == null || !header.startsWith("Second-")) return defaultValue;
        try {

            // May be Second-10
            // or
            // Second-10 Seconds

            int idx = header.indexOf(" ");
            if (idx != -1) {
                // up to the space
                header = header.substring(7, idx);
            } else {
                header = header.substring(7);
            }

            return Integer.parseInt(header);
        } catch (Exception e) {
            // warn about the parse failure and leave the timeout as the default
            s_logger.warn("Failed to parse " + name + " as a time");
            return defaultValue;
        }
    }

    protected Date getHeaderDate(String name) {
        String header = m_request.getHeader(name);
        if (header == null || header.length() == 0) return null;
        try {
            return new SimpleDateFormat(s_dateFormat).parse(header);
        } catch (Exception e) {
            return null;
        }
    }

    protected ArrayList<String> getETags(String name) {
        String value = m_request.getHeader(name);
        ArrayList<String> list = new ArrayList<String>();
        if (value == null) return list;

        StringTokenizer tokenizer = new StringTokenizer(value, ",");
        while (tokenizer.hasMoreTokens()) {
            list.add(tokenizer.nextToken().trim());
        }

        return list;
    }

    protected String getLockToken() throws MethodException {
        String strLockToken = null;
        String strIf = m_request.getHeader("Lock-Token");

        s_logger.info("Lock token Header: " + strIf);

        if (strIf != null && strIf.length() > 0) {
            strLockToken = strIf.substring("opaquelocktoken:".length() + 1, strIf.lastIndexOf(">"));
        }
        s_logger.info("Lock token Header = " + strLockToken + ".");
        return strLockToken;
    }

    protected String parseIfHeader() throws MethodException {
        String strLockToken = null;
        String strIf = m_request.getHeader("If");

        s_logger.info("Lock token Header: " + strIf);

        if (strIf != null && strIf.length() > 0) {
            if (strIf.startsWith("(<")) {
                // parse the tokens (only get the first one though)
                int idx = strIf.indexOf(">");
                if (idx != -1) {
                    try {
                        strLockToken = strIf.substring("opaquelocktoken:".length() + 2, idx);
                    } catch (IndexOutOfBoundsException e) {
                        s_logger.warn("Failed to parse If header: " + strIf);
                    }
                } else {
                    m_response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    throw new MethodException("Bad Request");
                }

                // print a warning if there are other tokens detected
                if (strIf.length() > idx + 2) {
                    s_logger.warn("The If header contained more than one lock token, only one is supported");
                }
            } else if (strIf.startsWith("<")) {
                s_logger.warn("Tagged lists in the If header are not supported");
            } else if (strIf.startsWith("([")) {
                s_logger.warn("ETags in the If header are not supported");
            }
        }

        return strLockToken;
    }

    protected String getBaseUrl() {
        return m_baseUrl;
    }

    protected String getPath() {
        return m_path;
    }

    protected String getUrl() {
        return m_baseUrl + m_path;
    }

    protected HttpServletRequest getRequest() {
        return m_request;
    }

    protected HttpServletResponse getResponse() {
        return m_response;
    }
}
