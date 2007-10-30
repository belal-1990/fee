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

package org.apache.geronimo.naming.deployment;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.geronimo.common.DeploymentException;
import org.apache.geronimo.gbean.AbstractNameQuery;
import org.apache.geronimo.gbean.GBeanData;
import org.apache.geronimo.gbean.GBeanInfo;
import org.apache.geronimo.gbean.GBeanInfoBuilder;
import org.apache.geronimo.j2ee.deployment.Module;
import org.apache.geronimo.j2ee.deployment.NamingBuilder;
import org.apache.geronimo.j2ee.j2eeobjectnames.NameFactory;
import org.apache.geronimo.kernel.ClassLoading;
import org.apache.geronimo.kernel.GBeanNotFoundException;
import org.apache.geronimo.naming.reference.GBeanReference;
import org.apache.geronimo.xbeans.geronimo.naming.GerGbeanRefDocument;
import org.apache.geronimo.xbeans.geronimo.naming.GerGbeanRefType;
import org.apache.geronimo.xbeans.geronimo.naming.GerPatternType;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.XmlObject;

/**
 * @version $Rev$ $Date$
 */
public class GBeanRefBuilder extends AbstractNamingBuilder {
    private static final QName GBEAN_REF_QNAME = GerGbeanRefDocument.type.getDocumentElementName();
    private static final QNameSet GBEAN_REF_QNAME_SET = QNameSet.singleton(GBEAN_REF_QNAME);

    public void buildNaming(XmlObject specDD, XmlObject plan, Module module, Map componentContext) throws DeploymentException {
        if (plan == null) {
            return;
        }
        XmlObject[] gbeanRefsUntyped = plan.selectChildren(GBEAN_REF_QNAME_SET);
        for (XmlObject gbeanRefUntyped : gbeanRefsUntyped) {
            GerGbeanRefType gbeanRef = (GerGbeanRefType) gbeanRefUntyped.copy().changeType(GerGbeanRefType.type);
            if (gbeanRef == null) {
                throw new DeploymentException("Could not read gbeanRef " + gbeanRefUntyped + " as the correct xml type");
            }
            GerPatternType[] gbeanLocatorArray = gbeanRef.getPatternArray();

            String[] interfaceTypesArray = gbeanRef.getRefTypeArray();
            Set<String> interfaceTypes = new HashSet<String>(Arrays.asList(interfaceTypesArray));
            Set<AbstractNameQuery> queries = new HashSet<AbstractNameQuery>();
            for (GerPatternType patternType : gbeanLocatorArray) {
                AbstractNameQuery abstractNameQuery = ENCConfigBuilder.buildAbstractNameQuery(patternType, null, null, interfaceTypes);
                queries.add(abstractNameQuery);
            }

            GBeanData gBeanData;
            try {
                gBeanData = module.getEarContext().getConfiguration().findGBeanData(queries);
            } catch (GBeanNotFoundException e) {
                throw new DeploymentException("Could not resolve reference at deploy time for queries " + queries, e);
            }

            if (interfaceTypes.isEmpty()) {
                interfaceTypes.add(gBeanData.getGBeanInfo().getClassName());
            }
            ClassLoader cl = module.getEarContext().getClassLoader();
            Class gBeanType;
            try {
                gBeanType = ClassLoading.loadClass(gBeanData.getGBeanInfo().getClassName(), cl);
            } catch (ClassNotFoundException e) {
                throw new DeploymentException("Cannot load GBean class", e);
            }

            String refName = gbeanRef.getRefName();

            NamingBuilder.JNDI_KEY.get(componentContext).put(ENV + refName, new GBeanReference(module.getConfigId(), queries, gBeanType));

        }
    }

    public QNameSet getSpecQNameSet() {
        return QNameSet.EMPTY;
    }

    public QNameSet getPlanQNameSet() {
        return GBEAN_REF_QNAME_SET;
    }

    public static final GBeanInfo GBEAN_INFO;

    static {
        GBeanInfoBuilder infoBuilder = GBeanInfoBuilder.createStatic(GBeanRefBuilder.class, NameFactory.MODULE_BUILDER);

        GBEAN_INFO = infoBuilder.getBeanInfo();
    }

    public static GBeanInfo getGBeanInfo() {
        return GBEAN_INFO;
    }

}
