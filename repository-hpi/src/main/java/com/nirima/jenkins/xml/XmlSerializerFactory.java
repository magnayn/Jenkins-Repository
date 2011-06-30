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
package com.nirima.jenkins.xml;

import com.nirima.jenkins.xml.impl.XMLOutputStreamWriter;

import javax.xml.stream.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;

public class XmlSerializerFactory
{
    static
    {
        System.setProperty("javax.xml.stream.XMLInputFactory", "com.ctc.wstx.stax.WstxInputFactory");
        System.setProperty("javax.xml.stream.XMLOutputFactory", "com.ctc.wstx.stax.WstxOutputFactory");
        System.setProperty("javax.xml.stream.XMLEventFactory", "com.ctc.wstx.stax.evt.WstxEventFactory");
    }

    public static XMLOutputStreamWriter create()
    {
        return new XMLOutputStreamWriter();
    }

    public static XMLStreamWriter create(OutputStream os) throws XmlSerializerException
    {
        try
        {
            XMLOutputFactory xof = XMLOutputFactory.newInstance();
            xof.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, true);
            return xof.createXMLStreamWriter(os);
        }
        catch (XMLStreamException e)
        {
            throw new XmlSerializerException("Problem Creating a pull parser", e);
        }
    }

    public static XMLStreamReader createXMLStreamReader(InputStream is) throws XmlSerializerException
    {
        try
        {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, "true");
            XMLStreamReader parser = factory.createXMLStreamReader(is);
            return parser;
        }
        catch (XMLStreamException e)
        {
            throw new XmlSerializerException("Problem Creating a pull parser", e);
        }
    }

    public static XMLStreamReader createXMLStreamReader(Reader reader) throws XmlSerializerException
    {

        try
        {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, true);
            // factory.setProperty(XMLInputFactory., true);
            XMLStreamReader parser = factory.createXMLStreamReader(reader);
            return parser;
        }
        catch (XMLStreamException e)
        {
            throw new XmlSerializerException("Problem Creating a pull parser reader", e);
        }
    }

    public static XMLStreamReader createXMLStreamReader(String xml) throws XmlSerializerException
    {
        StringReader sr = new StringReader(xml);
        return createXMLStreamReader(sr);
    }
}
