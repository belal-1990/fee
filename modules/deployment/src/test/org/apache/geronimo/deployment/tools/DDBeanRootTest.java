/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Geronimo" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Geronimo", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
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
 * ====================================================================
 */
package org.apache.geronimo.deployment.tools;

import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.io.FileNotFoundException;
import java.io.InputStream;
import javax.enterprise.deploy.model.DDBean;
import javax.enterprise.deploy.model.DDBeanRoot;
import javax.enterprise.deploy.model.DeployableObject;
import javax.enterprise.deploy.model.exceptions.DDBeanCreateException;
import javax.enterprise.deploy.shared.ModuleType;

import junit.framework.TestCase;

/**
 * 
 * 
 * @version $Revision: 1.1 $ $Date: 2004/01/21 20:37:28 $
 */
public class DDBeanRootTest extends TestCase {
    private DDBeanRoot root;
    private ClassLoader classLoader;

    public void testRoot() throws Exception {
        DeployableObject deployable = new MockDeployable();
        URL descriptor = classLoader.getResource("descriptors/app-client1.xml");
        root = new DDBeanRootImpl(deployable, descriptor);
        assertEquals("1.4", root.getDDBeanRootVersion());
        assertEquals(deployable, root.getDeployableObject());
        assertEquals(ModuleType.CAR, root.getType());
        assertEquals("/", root.getXpath());
        assertNull(root.getAttributeNames());
        assertNull(root.getText("foo"));
        assertTrue(Arrays.equals(new String[] {"Test DD for app-client1"}, root.getText("application-client/description")));
        assertTrue(Arrays.equals(new String[] {"http://localhost"}, root.getText("application-client/env-entry/env-entry-value")));
        assertTrue(Arrays.equals(new String[] {"url/test1", "url/test2"}, root.getText("application-client/env-entry/env-entry-name")));

        DDBean description = root.getChildBean("application-client/description")[0];
        assertEquals("Test DD for app-client1", description.getText());
        assertEquals("/application-client/description", description.getXpath());
        assertEquals(description, description.getChildBean("/application-client/description")[0]);
    }

    protected void setUp() throws Exception {
        classLoader = Thread.currentThread().getContextClassLoader();
    }

    private class MockDeployable implements DeployableObject {
        public Enumeration entries() {
            fail();
            throw new AssertionError();
        }

        public DDBean[] getChildBean(String xpath) {
            fail();
            throw new AssertionError();
        }

        public Class getClassFromScope(String className) {
            fail();
            throw new AssertionError();
        }

        public DDBeanRoot getDDBeanRoot() {
            fail();
            throw new AssertionError();
        }

        public DDBeanRoot getDDBeanRoot(String filename) throws FileNotFoundException, DDBeanCreateException {
            fail();
            throw new AssertionError();
        }

        public InputStream getEntry(String name) {
            fail();
            throw new AssertionError();
        }

        public String getModuleDTDVersion() {
            fail();
            throw new AssertionError();
        }

        public String[] getText(String xpath) {
            fail();
            throw new AssertionError();
        }

        public ModuleType getType() {
            return ModuleType.CAR;
        }
    }
}
