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



import com.nirima.jenkins.webdav.interfaces.IMethod;
import com.nirima.jenkins.webdav.interfaces.IMethodFactory;
import com.nirima.jenkins.webdav.interfaces.MethodException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;import java.lang.Character;import java.lang.Class;import java.lang.Exception;import java.lang.String;


public class MethodFactory implements IMethodFactory {
    public IMethod createMethod(HttpServletRequest request, HttpServletResponse response) throws MethodException {


        String methodName = request.getMethod();

        try {
            //      IDavContext dctx = createDavContext(request, response);
            // log.info("DAV Request: " + methodName);

            char first = Character.toUpperCase(methodName.charAt(0));
            String impl = "com.nirima.jenkins.webdav.impl.methods." + first + methodName.substring(1).toLowerCase();

            try {
                Class c = Class.forName(impl);
                IMethod method = (IMethod) c.newInstance();
                //method.init(request, response, null, repo, root);
                return method;
            } catch (Exception e) {
                throw new MethodException("Exception in creating method " + methodName, e);
            }
        } catch (Exception e) {
            throw new MethodException("Exception in creating method " + methodName, e);
        }
    }

}
