/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:  
 *       "This product includes software developed by the 
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Tomcat", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */ 
 
package javax.servlet.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * The HttpJspPage interface describes the interaction that a JSP Page
 * Implementation Class must satisfy when using the HTTP protocol.
 *
 * <p>
 * The behaviour is identical to that of the JspPage, except for the signature
 * of the _jspService method, which is now expressible in the Java type
 * system and included explicitly in the interface.
 * 
 * @see JspPage
 */

public interface HttpJspPage extends JspPage {

    /** The _jspService()method corresponds to the body of the JSP page. This
     * method is defined automatically by the JSP container and should never
     * be defined by the JSP page author.
     * <p>
     * If a superclass is specified using the extends attribute, that
     * superclass may choose to perform some actions in its service() method
     * before or after calling the _jspService() method.  See using the extends
     * attribute in the JSP_Engine chapter of the JSP specification.
     *
     * @param request Provides client request information to the JSP.
     * @param response Assists the JSP in sending a response to the client.
     * @throws ServletException Thrown if an error occurred during the 
     *     processing of the JSP and that the container should take 
     *     appropriate action to clean up the request.
     * @throws IOException Thrown if an error occurred while writing the
     *     response for this page.
     */
    public void _jspService(HttpServletRequest request,
                            HttpServletResponse response)
       throws ServletException, IOException;
}
