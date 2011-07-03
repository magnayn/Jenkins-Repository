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
package com.nirima.jenkins;


import com.sun.org.apache.xpath.internal.XPathAPI;
import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpClientConnection;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.*;
import org.apache.maven.artifact.Artifact;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;


public class SimpleArtifactCopier implements IArtifactCopier {



    URL host;
    File localRepo;

    private DefaultHttpClientConnection conn;
    private HttpHost targetHost;
    private HttpContext context;
    private BasicHttpParams params;
    private HttpRequestExecutor httpexecutor;
    private BasicHttpProcessor httpproc;
    private DefaultConnectionReuseStrategy connStrategy;

    public SimpleArtifactCopier(URL host, File localRepo) throws URISyntaxException {
        this.host = host;
        this.localRepo = localRepo;
        init();
    }

    private void init() throws URISyntaxException {
        URI targetURI = host.toURI();


        targetHost = new HttpHost(
                targetURI.getHost(),
                targetURI.getPort());

        params = new BasicHttpParams();
        params.setParameter(HttpProtocolParams.PROTOCOL_VERSION,
                HttpVersion.HTTP_1_1);
        params.setBooleanParameter(HttpProtocolParams.USE_EXPECT_CONTINUE,
                false);
        params.setBooleanParameter(HttpConnectionParams.STALE_CONNECTION_CHECK,
                false);
        params.setIntParameter(HttpConnectionParams.SOCKET_BUFFER_SIZE,
                8 * 1024);


        httpexecutor = new HttpRequestExecutor();
        httpproc = new BasicHttpProcessor();
        // Required protocol interceptors
        httpproc.addInterceptor(new RequestContent());
        httpproc.addInterceptor(new RequestTargetHost());
        // Recommended protocol interceptors
        httpproc.addInterceptor(new RequestConnControl());
        httpproc.addInterceptor(new RequestUserAgent());
        httpproc.addInterceptor(new RequestExpectContinue());

        context = new BasicHttpContext();

        conn = new DefaultHttpClientConnection();

        connStrategy = new DefaultConnectionReuseStrategy();
    }

    public void updateAll(Artifact art) throws IOException, HttpException, URISyntaxException, ParserConfigurationException, SAXException, TransformerException {

        List<String> items = fetchFiles(art);
        for (String item : items) {
            fetchFile(art, item);
        }
    }

    protected void fetchFile(Artifact art, String path) throws IOException, URISyntaxException, UnsupportedEncodingException, UnknownHostException, HttpException, TransformerException, SAXException, ParserConfigurationException {


        BasicHttpEntityEnclosingRequest httpget = new BasicHttpEntityEnclosingRequest("GET", path);

        // Start

        if (!conn.isOpen()) {
            Socket socket = new Socket(
                    targetHost.getHostName(),
                    targetHost.getPort() > 0 ? targetHost.getPort() : 80);
            conn.bind(socket, params);
        }

        context.setAttribute(ExecutionContext.HTTP_CONNECTION, conn);
        context.setAttribute(ExecutionContext.HTTP_TARGET_HOST, targetHost);

        httpexecutor.preProcess(httpget, httpproc, context);
        HttpResponse response = httpexecutor.execute(httpget, conn, context);
        httpexecutor.postProcess(response, httpproc, context);

        HttpEntity entity = response.getEntity();
        if (entity != null) {
            InputStream instream = entity.getContent();
            try {


                if (instream != null) {

                    File outFile = new File(localRepo, art.getGroupId().replace('.','/') + "/" + art.getArtifactId() + "/" + art.getVersion() );
                    outFile.mkdirs();
                    outFile = new File(outFile, path.substring(path.lastIndexOf('/')+1));

                    FileOutputStream fos = new FileOutputStream(outFile);
                    IOUtils.copy(instream, fos);
                    fos.close();

                }

            } catch (IOException ex) {
                conn.shutdown();

            } finally {
                instream.close();
            }
        }
        if (!connStrategy.keepAlive(response, context)) {
            conn.close();
        }


    }

    protected List<String> fetchFiles(Artifact art) throws IOException, URISyntaxException, UnsupportedEncodingException, UnknownHostException, HttpException, TransformerException, SAXException, ParserConfigurationException {

        List<String> entries = null;

        URL url = new URL(host, art.getGroupId().replace('.','/') + "/" + art.getArtifactId() + "/" + art.getVersion() + "/");

        //url = new URL(url, "LastSuccessful/repository");
        BasicHttpEntityEnclosingRequest httpget = new BasicHttpEntityEnclosingRequest("PROPFIND", url.toURI().getPath());
        //System.out.println("url=" + url);
        // HEADER
        String s = "<propget><allprop/></propget>";
        StringEntity se = new StringEntity(s, "US-ASCII");
        se.setChunked(false);
        httpget.setEntity(se);

        // Start

        if (!conn.isOpen()) {
            Socket socket = new Socket(
                    targetHost.getHostName(),
                    targetHost.getPort() > 0 ? targetHost.getPort() : 80);
            conn.bind(socket, params);
        }

        context.setAttribute(ExecutionContext.HTTP_CONNECTION, conn);
        context.setAttribute(ExecutionContext.HTTP_TARGET_HOST, targetHost);

        httpexecutor.preProcess(httpget, httpproc, context);
        HttpResponse response = httpexecutor.execute(httpget, conn, context);
        httpexecutor.postProcess(response, httpproc, context);

        HttpEntity entity = response.getEntity();
        if (entity != null) {
            InputStream instream = entity.getContent();
            try {


                if (instream != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    IOUtils.copy(instream, baos);


                    entries = getEntries(baos.toByteArray());
                }

            } catch (IOException ex) {
                conn.shutdown();

            } finally {
                instream.close();
            }
        }
        if (!connStrategy.keepAlive(response, context)) {
            conn.close();
        }


        // End

        return entries;
    }

    private List<String> getEntries(byte[] string) throws ParserConfigurationException, IOException, SAXException, TransformerException {

        List<String> items = new ArrayList<String>();


        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();

        //parse using builder to get DOM representation of the XML file
        Document dom = db.parse(new ByteArrayInputStream(string));

        NodeList l = XPathAPI.selectNodeList(dom, "//a:response");

        for (int i = 0; i < l.getLength(); i++) {
            Node n = l.item(i);

            Node ref = XPathAPI.selectSingleNode(n, "a:href");
            Node type = XPathAPI.selectSingleNode(n, "a:propstat/a:prop/a:resourcetype").getFirstChild();
            //System.out.println(ref.getTextContent());
//
//            System.out.println(type);

            // Just add files
            if (type == null)
                items.add(ref.getTextContent());
        }

        return items;
    }
}
