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
package com.nirima.jenkins.webdav.impl;

import com.nirima.jenkins.webdav.interfaces.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author nigel.magnay
 */
public class DAVItemSerializer
{
    private static final Logger log                    = LoggerFactory.getLogger(DAVItemSerializer.class);
    public static final String  dateFormatString       = "EEE, dd MMM yyyy HH:mm:ss zzz";
    public static final String  createDateFormatString = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    public void generateLockProperties(IDavItem item, XMLStreamWriter response) throws IOException
    {

    }

    public void generateNamedProperties(IDavContext ctxt, IDavItem item, XMLStreamWriter response, ArrayList<DavProperty> properties)
            throws IOException, XMLStreamException
    {
        ArrayList<DavProperty> missingProperties = new ArrayList<DavProperty>();
        response.writeStartElement("DAV:", "propstat");
        response.writeStartElement("DAV:", "prop");
        for (DavProperty prop : properties)
        {
            if (prop.getNamespace().equals("DAV:"))
            {
                if (prop.getProperty().equals("lockdiscovery") && item.isLocked(ctxt))
                {
                    // TODO
                }
                else if (prop.getProperty().equals("supportedlock"))
                    printLockSupport(response, item);
                else if (prop.getProperty().equals("resourcetype"))
                    printResourceType(response, item);
                else if (prop.getProperty().equals("displayname"))
                    printDisplayName(response, item);
                else if (prop.getProperty().equals("source"))
                    writeEmptyElement(response, "DAV:", "source");
                else if (prop.getProperty().equals("getlastmodified"))
                    printLastModified(response, item);
                else if (prop.getProperty().equals("getcontentlanguage") && item instanceof IDavFile)
                    printContentLanguage(response, (IDavFile) item);
                else if (prop.getProperty().equals("getcontenttype") && item instanceof IDavFile)
                    printContentType(response, (IDavFile) item);
                else if (prop.getProperty().equals("getetag") && item instanceof IDavFile)
                    printETag(response, (IDavFile) item);
                else if (prop.getProperty().equals("getcontentlength"))
                    printContentLength(response, item);
                else if (prop.getProperty().equals("creationdate"))
                    printCreationDate(response, item, new SimpleDateFormat(createDateFormatString));
                else
                    missingProperties.add(prop);
            }
            else
            {
                missingProperties.add(prop);
            }
        }
        response.writeEndElement();
        writeSimpleElement(response, "DAV:", "status", "HTTP/1.1 200 OK");
        response.writeEndElement();

        // Maybe some weren't available ?
        if (missingProperties.size() == 0) return;

        response.writeStartElement("DAV:", "propstat");
        response.writeStartElement("DAV:", "prop");
        for (DavProperty prop : missingProperties)
        {
            writeEmptyElement(response, prop.getNamespace(), prop.getProperty());
        }
        response.writeEndElement();
        writeSimpleElement(response, "DAV:", "status", "HTTP/1.1 " + HttpServletResponse.SC_NOT_FOUND + " Not Found");
        response.writeEndElement();

    }

    private void printLockSupport(XMLStreamWriter response, IDavItem item) throws IOException, XMLStreamException
    {
        // <supportedlock>" +
        // <lockentry>
        // <lockscope>
        // <exclusive/>
        // </lockscope>
        // <locktype>
        // <write/>
        // </locktype>
        // </lockentry>" +
        // </supportedlock>";

        if (item.getRepo().supportsLocking())
        {
            response.writeStartElement("DAV:", "supportedlock");
            response.writeStartElement("DAV:", "lockentry");
            response.writeStartElement("DAV:", "lockscope");
            response.writeStartElement("DAV:", "exclusive");
            response.writeEndElement();
            response.writeEndElement();

            response.writeStartElement("DAV:", "locktype");
            response.writeStartElement("DAV:", "write");
            response.writeEndElement();
            response.writeEndElement();
            response.writeEndElement();

            response.writeEndElement();
        }

    }

    private void printContentLength(XMLStreamWriter response, IDavItem item) throws IOException, XMLStreamException
    {
        response.writeStartElement("DAV:", "getcontentlength");
        if (item instanceof IDavCollection)
        {
            response.writeCharacters("0");
        }
        else
        {
            response.writeCharacters(Long.toString(((IDavFile) item).getContentLength()));
        }
        response.writeEndElement();
    }

    private void printResourceType(XMLStreamWriter response, IDavItem item) throws IOException, XMLStreamException
    {
        if (item instanceof IDavCollection)
        {
            response.writeStartElement("DAV:", "resourcetype");
            response.writeStartElement("DAV:", "collection");
            response.writeEndElement();
            response.writeEndElement();
        }
        else
        {
            response.writeStartElement("DAV:", "resourcetype");
            response.writeEndElement();
        }
    }

    public void generateLockXml(XMLStreamWriter response, IDavLock item) throws IOException, XMLStreamException
    {
        response.writeStartElement("DAV:", "lockdiscovery");
        response.writeStartElement("DAV:", "activelock");

        response.writeStartElement("DAV:", "locktype");
        writeEmptyElement(response, "DAV:", "write");
        response.writeEndElement();

        response.writeStartElement("DAV:", "lockscope");
        writeEmptyElement(response, "DAV:", "exclusive");
        response.writeEndElement();

        writeSimpleElement(response, "DAV:", "depth", Integer.toString(item.getDepth()));
        writeSimpleElement(response, "DAV:", "owner", item.getOwner());

        Date expires = item.getExpiration();

        if (expires == null)
        {
            writeSimpleElement(response, "DAV:", "timeout", "Infinite");
        }
        else
        {
            long now = new Date().getTime();
            long secsRemain = (expires.getTime() - now) / 1000;
            writeSimpleElement(response, "DAV:", "timeout", "Second-" + secsRemain);
        }

        response.writeStartElement("DAV:", "locktoken");
        writeSimpleElement(response, "DAV:", "href", "opaquelocktoken:" + item.getToken());
        response.writeEndElement(); // locktoken

        response.writeEndElement(); // activelock

        response.writeEndElement(); // lockdiscovery
    }

    /*
     * protected void generateLockDiscoveryXML(XMLPrinter responseOutput, LockMetaData lockData) throws Exception { if
     * (lockData != null) { responseOutput.writeElement(null, WebDAVUtils.XML_LOCK_DISCOVERY, XMLPrinter.OPENING);
     * responseOutput.writeElement(null, WebDAVUtils.XML_ACTIVE_LOCK, XMLPrinter.OPENING);
     * responseOutput.writeElement(null, WebDAVUtils.XML_LOCK_TYPE, XMLPrinter.OPENING); responseOutput.writeElement(null,
     * WebDAVUtils.XML_WRITE, XMLPrinter.NO_CONTENT); responseOutput.writeElement(null, WebDAVUtils.XML_LOCK_TYPE,
     * XMLPrinter.CLOSING); responseOutput.writeElement(null, WebDAVUtils.XML_LOCK_SCOPE, XMLPrinter.OPENING);
     * responseOutput.writeElement(null, WebDAVUtils.XML_EXCLUSIVE, XMLPrinter.NO_CONTENT); // NOTE: we only do exclusive
     * lock tokens at the moment //responseOutput.writeElement(null, WebDAVUtils.XML_SHARED, XMLPrinter.NO_CONTENT);
     * responseOutput.writeElement(null, WebDAVUtils.XML_LOCK_SCOPE, XMLPrinter.CLOSING);
     * responseOutput.writeElement(null, WebDAVUtils.XML_DEPTH, XMLPrinter.OPENING); // NOTE: we only support one level of
     * lock at the moment //responseOutput.writeText("Infinity"); responseOutput.writeText(WebDAVUtils.ZERO);
     * responseOutput.writeElement(null, WebDAVUtils.XML_DEPTH, XMLPrinter.CLOSING); responseOutput.writeElement(null,
     * WebDAVUtils.XML_OWNER, XMLPrinter.OPENING); responseOutput.writeData(lockData.getOwner());
     * responseOutput.writeElement(null, WebDAVUtils.XML_OWNER, XMLPrinter.CLOSING); responseOutput.writeElement(null,
     * WebDAVUtils.XML_TIMEOUT, XMLPrinter.OPENING); Date expires = lockData.getExpirationDate(); String strTimeout =
     * WebDAVUtils.INFINITE; if (expires != null) { long now = new Date().getTime(); long expiresMillis =
     * expires.getTime(); long timeoutRemaining = (expiresMillis - now)/1000; strTimeout = WebDAVUtils.SECOND +
     * timeoutRemaining; } responseOutput.writeText(strTimeout); responseOutput.writeElement(null,
     * WebDAVUtils.XML_TIMEOUT, XMLPrinter.CLOSING); responseOutput.writeElement(null, WebDAVUtils.XML_LOCK_TOKEN,
     * XMLPrinter.OPENING); responseOutput.writeElement(null, WebDAVUtils.XML_HREF, XMLPrinter.OPENING); String
     * strLockToken = lockData.getToken(); responseOutput.writeText(WebDAVUtils.OPAQUE_LOCK_TOKEN + strLockToken);
     * responseOutput.writeElement(null, WebDAVUtils.XML_HREF, XMLPrinter.CLOSING); responseOutput.writeElement(null,
     * WebDAVUtils.XML_LOCK_TOKEN, XMLPrinter.CLOSING); responseOutput.writeElement(null, WebDAVUtils.XML_ACTIVE_LOCK,
     * XMLPrinter.CLOSING); responseOutput.writeElement(null, WebDAVUtils.XML_LOCK_DISCOVERY, XMLPrinter.CLOSING); }
     */

    private void printContentLanguage(XMLStreamWriter response, IDavFile item) throws IOException, XMLStreamException
    {
        writeSimpleElement(response, "DAV:", "getcontentlanguage", item.getContentLanguage());

    }

    private void printContentType(XMLStreamWriter response, IDavFile item) throws IOException, XMLStreamException
    {
        writeSimpleElement(response, "DAV:", "getcontenttype", item.getContentType());

    }

    private void printDisplayName(XMLStreamWriter response, IDavItem item) throws IOException, XMLStreamException
    {
        log.debug("Trying to get");
        String itemName = item.getName();
        log.debug("got " + itemName);
        if (itemName == null) itemName = "null";
        writeSimpleElement(response, "DAV:", "displayname", itemName);
    }

    private void printETag(XMLStreamWriter response, IDavFile item) throws IOException, XMLStreamException
    {
        writeSimpleElement(response, "DAV:", "getetag", item.getETag());
    }

    private void printLastModified(XMLStreamWriter response, IDavItem item) throws IOException, XMLStreamException
    {
        writeSimpleElement(response, "DAV:", "getlastmodified", new SimpleDateFormat(dateFormatString).format(item.getLastModified()));
    }

    private void printCreationDate(XMLStreamWriter response, IDavItem item, SimpleDateFormat formatter) throws IOException, XMLStreamException
    {
        writeSimpleElement(response, "DAV:", "creationdate", formatter.format(item.getCreationDate()));
    }

    public void generateProperties(IDavContext ctxt, IDavItem item, XMLStreamWriter response, boolean created) throws IOException, XMLStreamException
    {
        response.writeStartElement("DAV:", "propstat");
        response.writeStartElement("DAV:", "prop");
        if (item.isLocked(ctxt)) generateLockProperties(item, response);

        printLockSupport(response, item);

        printResourceType(response, item);
        printDisplayName(response, item);
        writeEmptyElement(response, "DAV:", "source");

        printLastModified(response, item);

        if (item instanceof IDavFile)
        {
            printContentLanguage(response, (IDavFile) item);
            printContentType(response, (IDavFile) item);
            printETag(response, (IDavFile) item);
        }

        printContentLength(response, item);
        SimpleDateFormat creationDateFormat = new SimpleDateFormat(createDateFormatString);
        printCreationDate(response, item, creationDateFormat);

        writeSimpleElement(response, "DAV:", "creationdate", creationDateFormat.format(item.getCreationDate()));

        // TODO: Custom properties here...

        response.writeEndElement();
        if (created)
            writeSimpleElement(response, "DAV:", "status", "HTTP/1.1 201 Created");
        else
            writeSimpleElement(response, "DAV:", "status", "HTTP/1.1 200 OK");
        response.writeEndElement();
    }

    protected XMLStreamWriter writeSimpleElement(XMLStreamWriter response, String ns, String name, String value) throws IOException, XMLStreamException
    {
        response.writeStartElement(ns, name);
        response.writeCharacters(value);
        response.writeEndElement();

        return response;
    }

    protected XMLStreamWriter writeEmptyElement(XMLStreamWriter response, String ns, String name) throws IOException, XMLStreamException
    {
        response.writeStartElement(ns, name);
        response.writeEndElement();

        return response;
    }

}
