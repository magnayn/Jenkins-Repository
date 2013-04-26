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
import com.nirima.jenkins.webdav.impl.DavProperty;
import com.nirima.jenkins.webdav.interfaces.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author nigelm
 */
public class Propfind extends MethodBase {

    private static Logger s_logger = LoggerFactory.getLogger(Propfind.class);
    private ArrayList<DavProperty> m_properties;
    private int m_depth = 0;

    /*
     * (non-Javadoc)
     * 
     * @see nrm.webdav.interfaces.IMethod#invoke()
     */
    @Override
    public void invoke(IDavContext ctxt) throws MethodException {
        try {
            s_logger.info("propfind invoke");

            m_depth = getHeaderInt("Depth", 0);

            s_logger.info("Depth = " + m_depth);

            XMLStreamReader xpp = getDocument();

            if (xpp == null) {
                invokeInternal(ctxt);
                return;
            }

            for (int eventType = xpp.getEventType(); eventType != XMLStreamConstants.END_DOCUMENT; eventType = xpp.next()) {
                if (eventType == XMLStreamConstants.START_ELEMENT) {
                    String name = xpp.getName().getLocalPart();
                    s_logger.info("rcvd " + name + " " + xpp.getName().getNamespaceURI());
                    if (name.equals("propfind")) {
                        // Propfind has a subnode
                    }
                    if (name.equals("prop")) {
                        // Named
                        m_properties = getRequiredProperties(xpp);
                        invokeInternal(ctxt);
                    } else if (name.equals("allprop")) {
                        invokeInternal(ctxt);
                    } else if (name.equals("propname")) {
                        // TODO
                        s_logger.info("*** TODO propname " + name + " " + xpp.getName().getNamespaceURI());

                    }

                }

            }
        } catch (Exception e) {
            throw new MethodException("Error", e);
        }
    }

    protected void invokeInternal(IDavContext ctxt) throws IOException, MethodException, XMLStreamException {

        IDavItem topLevelItem;

        topLevelItem = getRepo().getItem(getDavContext(), this.getPath());


        if (topLevelItem == null) {
            this.getResponse().setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        this.getResponse().setStatus(WEBDAV_MULTI_STATUS);
        this.getResponse().setContentType("text/xml; charset=UTF-8");

        XMLStreamWriter response = this.createXmlResponse();
        //response.writeStartDocument("UTF-8");
        response.setPrefix("a", "DAV:");

        response.writeStartElement("DAV:", "multistatus");

        if (topLevelItem instanceof IDavFile)
            printItem(ctxt, response, topLevelItem);
        else {

            // if (depth == 0 && topLevelItem.getPath().equals(getRepo().getRepositoryRoot().getPath()))
            // {
            // Initial connection call from Windows. Set the depth to 1 so it succeeds
            // depth = 1;
            // }

            if (m_depth == 0) {
                printItem(ctxt, response, topLevelItem);
            } else {
                Collection<IDavItem> items = getRepo().getItems(getDavContext(), topLevelItem, m_depth);

                for (IDavItem di : items) {
                    printItem(ctxt, response, di);
                }
            }

        }

        response.writeEndElement();

        response.writeEndDocument();
        response.flush();
        response.close();

    }

    protected void printItem(IDavContext ctxt, XMLStreamWriter response, IDavItem di) throws IOException, MethodException, XMLStreamException {
        DAVItemSerializer dis = new DAVItemSerializer();

        // Item
        response.writeStartElement("DAV:", "response");
        response.writeStartElement("DAV:", "href");

        String href = this.getBaseUrl();

        // If we're looking at the root item, don't ask it for its' name, because it will
        // be 'null'
        if (!di.equals(getRepo().getRepositoryRoot(getDavContext()))) href += di.getPath(ctxt);
        // else
        // href += "/";

        // The following is to keep slide happy. I think it's wrong (looking at the RFC examples
        // seems to confirm this), but - hey ho!
        URL url = new URL(href);
        href = url.getPath();

        s_logger.info("PATH href " + href);
        response.writeCharacters(href);

        response.writeEndElement();
        if (m_properties != null)
            dis.generateNamedProperties(ctxt, di, response, m_properties);
        else
            dis.generateProperties(ctxt, di, response, false);
        response.writeEndElement();

        // Item
    }

    protected ArrayList<DavProperty> getRequiredProperties(XMLStreamReader xpp) throws IOException, XMLStreamException {
        ArrayList<DavProperty> properties = new ArrayList<DavProperty>();
        String name = xpp.getName().getLocalPart();
        for (int eventType = xpp.next(); !(eventType == XMLStreamConstants.END_ELEMENT && xpp.getName().getLocalPart().equals(name))
                && eventType != XMLStreamConstants.END_DOCUMENT; eventType = xpp.next()) {
            if (eventType == XMLStreamConstants.START_ELEMENT) {
                String ns = xpp.getName().getNamespaceURI();
                String prop = xpp.getName().getLocalPart();

                s_logger.info("Asking for property " + ns + ", " + prop);
                properties.add(new DavProperty(ns, prop));
            }
        }
        return properties;
    }
}
