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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author nigelm
 */
public class Get extends Head {

    @Override
    protected void writeContent(IDavFile fileItem) throws IOException {
        InputStream is = fileItem.getContent();
        BufferedOutputStream os = new BufferedOutputStream(this.getResponse().getOutputStream());

        // TODO: Change this to commons-io

        byte[] buf = new byte[10240];
        int read = -1;
        while ((read = is.read(buf)) > 0) {
            os.write(buf, 0, read);
        }
        os.flush();
        is.close();
    }
}
