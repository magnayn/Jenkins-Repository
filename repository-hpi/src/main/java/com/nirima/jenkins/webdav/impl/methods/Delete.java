/*
 * Created on 16-Mar-2005 TODO To change the template for this generated file go to Window - Preferences - Java - Code
 * Style - Code Templates
 */
package com.nirima.jenkins.webdav.impl.methods;

import com.nirima.jenkins.webdav.interfaces.IDavContext;
import com.nirima.jenkins.webdav.interfaces.IDavItem;
import com.nirima.jenkins.webdav.interfaces.IDavRepo;
import com.nirima.jenkins.webdav.interfaces.MethodException;


import javax.servlet.http.HttpServletResponse;

/**
 * @author nigelm
 */
public class Delete extends MethodBase {

    /*
     * (non-Javadoc)
     * 
     * @see nrm.webdav.interfaces.IMethod#invoke()
     */
    @Override
    public void invoke(IDavContext ctxt) throws MethodException {
        try {
            IDavRepo repo = getRepo();

            IDavItem item = repo.getItem(getDavContext(), this.getPath());

            if (item != null) {
                // Remove this option for now
                item.delete(getDavContext());
            }

            this.getResponse().setStatus(HttpServletResponse.SC_NO_CONTENT);

        } catch (Exception e) {
            throw new MethodException("Error deleting object", e);
        }

    }

}
