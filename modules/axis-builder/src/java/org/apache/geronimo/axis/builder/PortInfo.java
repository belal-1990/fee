/**
 *
 * Copyright 2003-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.geronimo.axis.builder;

import javax.xml.namespace.QName;
import javax.wsdl.Definition;
import javax.wsdl.Port;

import org.apache.geronimo.xbeans.j2ee.JavaWsdlMappingType;
import org.apache.geronimo.xbeans.j2ee.PortComponentHandlerType;
import org.apache.geronimo.xbeans.j2ee.ServiceEndpointInterfaceMappingType;

/**
 * @version $Rev:  $ $Date:  $
 */
public class PortInfo {
    private final String portName;
    private final QName portQName;
    private final Definition definition;
    private final JavaWsdlMappingType javaWsdlMapping;
    private final ServiceEndpointInterfaceMappingType seiMapping;
    private final String seInterfaceName;
    private final PortComponentHandlerType[] handlers;
    private final Port port;

    public PortInfo(String portName, QName portQName, Definition definition, JavaWsdlMappingType javaWsdlMapping, String seiInterfaceName, PortComponentHandlerType[] handlers, Port port, ServiceEndpointInterfaceMappingType seiMapping) {
        this.portName = portName;
        this.portQName = portQName;
        this.definition = definition;
        this.javaWsdlMapping = javaWsdlMapping;
        this.seInterfaceName = seiInterfaceName;
        this.handlers = handlers;
        this.port = port;
        this.seiMapping = seiMapping;
    }

    public String getPortName() {
        return getPortComponentName();
    }

    public String getPortComponentName() {
        return portName;
    }

    public QName getPortQName() {
        return portQName;
    }

    public Port getPort() {
        return port;
    }

    public Definition getDefinition() {
        return definition;
    }

    public JavaWsdlMappingType getJavaWsdlMapping() {
        return javaWsdlMapping;
    }

    public String getServiceEndpointInterfaceName() {
        return seInterfaceName;
    }


    public ServiceEndpointInterfaceMappingType getServiceEndpointInterfaceMapping() {
        return seiMapping;
    }

    public PortComponentHandlerType[] getHandlers() {
        return handlers;
    }
}
