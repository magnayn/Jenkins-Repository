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
package com.nirima.jenkins.xml.impl;

import com.nirima.jenkins.xml.XmlSerializerFactory;


import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * An XML Stream writer that provides access to the underlying output stream
 * 
 * @author nigelmagnay
 *
 */
public class XMLOutputStreamWriter implements XMLStreamWriter
{
    XMLStreamWriter xmlStream;
    OutputStream    outputStream;

    public XMLOutputStreamWriter()
    {
        outputStream = new ByteArrayOutputStream();
        xmlStream = XmlSerializerFactory.create(outputStream);
    }

    public @Override
    String toString()
    {
        try
        {
            xmlStream.flush();
            if( outputStream instanceof ByteArrayOutputStream )
            {
                return ((ByteArrayOutputStream)outputStream).toString("UTF-8");
            }
            else
            {
                return outputStream.toString();
            }
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Unexpected flush error", ex);
        }
    }

    public void element(String name, String data) throws XMLStreamException
    {
        this.writeStartElement(name);
        this.writeCharacters(data);
        this.writeEndElement();
    }

    public void rawData(String data) throws XMLStreamException, IOException
    {
        this.flush();
        outputStream.write(data.getBytes());
    }

    /* (non-Javadoc)
     * @see javax.xml.stream.XMLStreamWriter#close()
     */
    public void close() throws XMLStreamException
    {
        xmlStream.close();

    }

    /* (non-Javadoc)
     * @see javax.xml.stream.XMLStreamWriter#flush()
     */
    public void flush() throws XMLStreamException
    {
        xmlStream.flush();
    }

    /* (non-Javadoc)
     * @see javax.xml.stream.XMLStreamWriter#getNamespaceContext()
     */
    public NamespaceContext getNamespaceContext()
    {
        return xmlStream.getNamespaceContext();
    }

    /* (non-Javadoc)
     * @see javax.xml.stream.XMLStreamWriter#getPrefix(java.lang.String)
     */
    public String getPrefix(String arg0) throws XMLStreamException
    {
        return xmlStream.getPrefix(arg0);
    }

    /* (non-Javadoc)
     * @see javax.xml.stream.XMLStreamWriter#getProperty(java.lang.String)
     */
    public Object getProperty(String arg0) throws IllegalArgumentException
    {
        return xmlStream.getProperty(arg0);
    }

    /* (non-Javadoc)
     * @see javax.xml.stream.XMLStreamWriter#setDefaultNamespace(java.lang.String)
     */
    public void setDefaultNamespace(String arg0) throws XMLStreamException
    {
        xmlStream.setDefaultNamespace(arg0);

    }

    /* (non-Javadoc)
     * @see javax.xml.stream.XMLStreamWriter#setNamespaceContext(javax.xml.namespace.NamespaceContext)
     */
    public void setNamespaceContext(NamespaceContext arg0) throws XMLStreamException
    {
        xmlStream.setNamespaceContext(arg0);

    }

    /* (non-Javadoc)
     * @see javax.xml.stream.XMLStreamWriter#setPrefix(java.lang.String, java.lang.String)
     */
    public void setPrefix(String arg0, String arg1) throws XMLStreamException
    {
        xmlStream.setPrefix(arg0, arg1);
    }

    /* (non-Javadoc)
     * @see javax.xml.stream.XMLStreamWriter#writeAttribute(java.lang.String, java.lang.String)
     */
    public void writeAttribute(String arg0, String arg1) throws XMLStreamException
    {
        xmlStream.writeAttribute(arg0, arg1 != null ? arg1 : "");

    }

    /* (non-Javadoc)
     * @see javax.xml.stream.XMLStreamWriter#writeAttribute(java.lang.String, java.lang.String, java.lang.String)
     */
    public void writeAttribute(String arg0, String arg1, String arg2) throws XMLStreamException
    {
        xmlStream.writeAttribute(arg0, arg1, arg2);

    }

    /* (non-Javadoc)
     * @see javax.xml.stream.XMLStreamWriter#writeAttribute(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void writeAttribute(String arg0, String arg1, String arg2, String arg3) throws XMLStreamException
    {
        xmlStream.writeAttribute(arg0, arg1, arg2, arg3);
    }

    /* (non-Javadoc)
     * @see javax.xml.stream.XMLStreamWriter#writeCData(java.lang.String)
     */
    public void writeCData(String arg0) throws XMLStreamException
    {
        xmlStream.writeCData(arg0);
    }

    /* (non-Javadoc)
     * @see javax.xml.stream.XMLStreamWriter#writeCharacters(java.lang.String)
     */
    public void writeCharacters(String arg0) throws XMLStreamException
    {
        xmlStream.writeCharacters(arg0 != null ? arg0 : "");
    }

    /* (non-Javadoc)
     * @see javax.xml.stream.XMLStreamWriter#writeCharacters(char[], int, int)
     */
    public void writeCharacters(char[] arg0, int arg1, int arg2) throws XMLStreamException
    {
        xmlStream.writeCharacters(arg0, arg1, arg2);

    }

    /* (non-Javadoc)
     * @see javax.xml.stream.XMLStreamWriter#writeComment(java.lang.String)
     */
    public void writeComment(String arg0) throws XMLStreamException
    {
        xmlStream.writeComment(arg0);

    }

    /* (non-Javadoc)
     * @see javax.xml.stream.XMLStreamWriter#writeDTD(java.lang.String)
     */
    public void writeDTD(String arg0) throws XMLStreamException
    {
        xmlStream.writeDTD(arg0);

    }

    /* (non-Javadoc)
     * @see javax.xml.stream.XMLStreamWriter#writeDefaultNamespace(java.lang.String)
     */
    public void writeDefaultNamespace(String arg0) throws XMLStreamException
    {
        xmlStream.writeDefaultNamespace(arg0);

    }

    /* (non-Javadoc)
     * @see javax.xml.stream.XMLStreamWriter#writeEmptyElement(java.lang.String)
     */
    public void writeEmptyElement(String arg0) throws XMLStreamException
    {
        xmlStream.writeEmptyElement(arg0);

    }

    /* (non-Javadoc)
     * @see javax.xml.stream.XMLStreamWriter#writeEmptyElement(java.lang.String, java.lang.String)
     */
    public void writeEmptyElement(String arg0, String arg1) throws XMLStreamException
    {
        xmlStream.writeEmptyElement(arg0, arg1);
    }

    /* (non-Javadoc)
     * @see javax.xml.stream.XMLStreamWriter#writeEmptyElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public void writeEmptyElement(String arg0, String arg1, String arg2) throws XMLStreamException
    {
        xmlStream.writeEmptyElement(arg0, arg1, arg2);

    }

    /* (non-Javadoc)
     * @see javax.xml.stream.XMLStreamWriter#writeEndDocument()
     */
    public void writeEndDocument() throws XMLStreamException
    {
        xmlStream.writeEndDocument();

    }

    /* (non-Javadoc)
     * @see javax.xml.stream.XMLStreamWriter#writeEndElement()
     */
    public void writeEndElement() throws XMLStreamException
    {
        xmlStream.writeEndElement();

    }

    /* (non-Javadoc)
     * @see javax.xml.stream.XMLStreamWriter#writeEntityRef(java.lang.String)
     */
    public void writeEntityRef(String arg0) throws XMLStreamException
    {
        xmlStream.writeEntityRef(arg0);

    }

    /* (non-Javadoc)
     * @see javax.xml.stream.XMLStreamWriter#writeNamespace(java.lang.String, java.lang.String)
     */
    public void writeNamespace(String arg0, String arg1) throws XMLStreamException
    {
        xmlStream.writeNamespace(arg0, arg1);

    }

    /* (non-Javadoc)
     * @see javax.xml.stream.XMLStreamWriter#writeProcessingInstruction(java.lang.String)
     */
    public void writeProcessingInstruction(String arg0) throws XMLStreamException
    {
        xmlStream.writeProcessingInstruction(arg0);

    }

    /* (non-Javadoc)
     * @see javax.xml.stream.XMLStreamWriter#writeProcessingInstruction(java.lang.String, java.lang.String)
     */
    public void writeProcessingInstruction(String arg0, String arg1) throws XMLStreamException
    {
        xmlStream.writeProcessingInstruction(arg0, arg1);

    }

    /* (non-Javadoc)
     * @see javax.xml.stream.XMLStreamWriter#writeStartDocument()
     */
    public void writeStartDocument() throws XMLStreamException
    {
        xmlStream.writeStartDocument();

    }

    /* (non-Javadoc)
     * @see javax.xml.stream.XMLStreamWriter#writeStartDocument(java.lang.String)
     */
    public void writeStartDocument(String arg0) throws XMLStreamException
    {
        xmlStream.writeStartDocument(arg0);

    }

    /* (non-Javadoc)
     * @see javax.xml.stream.XMLStreamWriter#writeStartDocument(java.lang.String, java.lang.String)
     */
    public void writeStartDocument(String arg0, String arg1) throws XMLStreamException
    {
        xmlStream.writeStartDocument(arg0, arg1);

    }

    /* (non-Javadoc)
     * @see javax.xml.stream.XMLStreamWriter#writeStartElement(java.lang.String)
     */
    public void writeStartElement(String arg0) throws XMLStreamException
    {
        xmlStream.writeStartElement(arg0);

    }

    /* (non-Javadoc)
     * @see javax.xml.stream.XMLStreamWriter#writeStartElement(java.lang.String, java.lang.String)
     */
    public void writeStartElement(String arg0, String arg1) throws XMLStreamException
    {
        xmlStream.writeStartElement(arg0, arg1);

    }

    /* (non-Javadoc)
     * @see javax.xml.stream.XMLStreamWriter#writeStartElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public void writeStartElement(String arg0, String arg1, String arg2) throws XMLStreamException
    {
        xmlStream.writeStartElement(arg0, arg1, arg2);
    }

    /**
     * @return the outputStream
     */
    public OutputStream getOutputStream()
    {
        return outputStream;
    }

    /**
     * @param outputStream the outputStream to set
     */
    public void setOutputStream(OutputStream outputStream)
    {
        this.outputStream = outputStream;
    }

    /**
     * @return the xmlStream
     */
    public XMLStreamWriter getXmlStream()
    {
        return xmlStream;
    }

}
