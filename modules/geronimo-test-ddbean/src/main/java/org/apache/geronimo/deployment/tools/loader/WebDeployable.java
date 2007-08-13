/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.geronimo.deployment.tools.loader;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.enterprise.deploy.model.exceptions.DDBeanCreateException;
import javax.enterprise.deploy.shared.ModuleType;

import org.apache.geronimo.kernel.config.MultiParentClassLoader;

/**
 *
 *
 * @version $Rev$ $Date$
 */
public class WebDeployable extends AbstractDeployable {
    private final ClassLoader webLoader;

    public WebDeployable(URL moduleURL) throws DDBeanCreateException{
        this(moduleURL, null);
    }
    
    public WebDeployable(URL moduleURL, List parentClassLoaders) throws DDBeanCreateException {
        super(ModuleType.WAR, moduleURL, "WEB-INF/web.xml");
        ClassLoader parent = super.getModuleLoader();
        List path = new ArrayList();
        path.add(parent.getResource("WEB-INF/classes/"));
        Enumeration e = entries();
        while (e.hasMoreElements()) {
            String entry = (String) e.nextElement();
            if (entry.startsWith("WEB-INF/lib/")) {
                String jarName = entry.substring(12);
                if (jarName.indexOf('/') == -1 && (jarName.endsWith(".jar") || jarName.endsWith(".zip"))) {
                    path.add(parent.getResource(entry));
                }
            }
        }
        URL[] urls = (URL[]) path.toArray(new URL[path.size()]);
        if (parentClassLoaders != null) {
            parentClassLoaders.add(parent);
            ClassLoader[] parents = (ClassLoader[]) parentClassLoaders.toArray(new ClassLoader[parentClassLoaders.size()]);
            webLoader = new MultiParentClassLoader(null, urls, parents);
        } else {
            webLoader = new URLClassLoader(urls, parent);
        }
    }

    public ClassLoader getModuleLoader() {
        return webLoader;
    }
}
