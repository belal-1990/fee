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
package org.apache.geronimo.deployment.model.geronimo.appclient;

import org.apache.geronimo.deployment.model.geronimo.j2ee.EjbLocalRef;
import org.apache.geronimo.deployment.model.geronimo.j2ee.EjbRef;
import org.apache.geronimo.deployment.model.geronimo.j2ee.JNDIEnvironmentRefs;
import org.apache.geronimo.deployment.model.geronimo.j2ee.MessageDestination;
import org.apache.geronimo.deployment.model.geronimo.j2ee.MessageDestinationRef;
import org.apache.geronimo.deployment.model.geronimo.j2ee.ResourceEnvRef;
import org.apache.geronimo.deployment.model.geronimo.j2ee.ResourceRef;
import org.apache.geronimo.deployment.model.geronimo.j2ee.ServiceRef;


/**
 * JavaBean for the geronimo-application-client.xml tag application-client
 *
 * @version $Revision: 1.4 $ $Date: 2003/10/07 19:16:31 $
 */
public class ApplicationClient extends org.apache.geronimo.deployment.model.appclient.ApplicationClient implements JNDIEnvironmentRefs {
    public EjbLocalRef[] getGeronimoEJBLocalRef() {
        return new EjbLocalRef[0];
    }

    public EjbLocalRef getGeronimoEJBLocalRef(int i) {
        return null;
    }

    public void setGeronimoEJBLocalRef(int i, EjbLocalRef ejbRef) {
        throw new UnsupportedOperationException("Application client does not support EJB local references.");
    }

    public void setGeronimoEJBLocalRef(EjbLocalRef[] ejbRef) {
        throw new UnsupportedOperationException("Application client does not support EJB local references.");
    }

    public void setEJBRef(org.apache.geronimo.deployment.model.j2ee.EJBRef[] ejbRef) {
        assert (ejbRef instanceof EjbRef[]);
        super.setEJBRef(ejbRef);
    }

    public void setEJBRef(int i, org.apache.geronimo.deployment.model.j2ee.EJBRef ejbRef) {
        assert (ejbRef instanceof EjbRef);
        super.setEJBRef(i, ejbRef);
    }

    public EjbRef getGeronimoEJBRef(int i) {
        return (EjbRef) getEJBRef(i);
    }

    public void setGeronimoEJBRef(int i, EjbRef ejbRef) {
        super.setEJBRef(i, ejbRef);
    }

    public EjbRef[] getGeronimoEJBRef() {
        return (EjbRef[]) getEJBRef();
    }

    public void setGeronimoEJBRef(EjbRef[] ejbRef) {
        super.setEJBRef(ejbRef);
    }

    public void setMessageDestination(org.apache.geronimo.deployment.model.j2ee.MessageDestination[] messageDestination) {
        assert (messageDestination instanceof MessageDestination[]);
        super.setMessageDestination(messageDestination);
    }

    public void setMessageDestination(int i, org.apache.geronimo.deployment.model.j2ee.MessageDestination messageDestination) {
        assert (messageDestination instanceof MessageDestination);
        super.setMessageDestination(i, messageDestination);
    }

    public MessageDestination getGeronimoMessageDestination(int i) {
        return (MessageDestination) super.getMessageDestination(i);
    }

    public void setGeronimoMessageDestination(int i, MessageDestination messageDestination) {
        super.setMessageDestination(i, messageDestination);
    }

    public MessageDestination[] getGeronimoMessageDestination() {
        return (MessageDestination[]) super.getMessageDestination();
    }

    public void setGeronimoMessageDestination(MessageDestination[] messageDestination) {
        super.setMessageDestination(messageDestination);
    }

    public void setMessageDestinationRef(org.apache.geronimo.deployment.model.j2ee.MessageDestinationRef[] messageDestinationRef) {
        assert (messageDestinationRef instanceof MessageDestinationRef[]);
        super.setMessageDestinationRef(messageDestinationRef);
    }

    public void setMessageDestinationRef(int i, org.apache.geronimo.deployment.model.j2ee.MessageDestinationRef messageDestinationRef) {
        assert (messageDestinationRef instanceof MessageDestinationRef);
        super.setMessageDestinationRef(i, messageDestinationRef);
    }

    public MessageDestinationRef getGeronimoMessageDestinationRef(int i) {
        return (MessageDestinationRef) super.getMessageDestinationRef(i);
    }

    public void setGeronimoMessageDestinationRef(int i, MessageDestinationRef messageDestinationRef) {
        super.setMessageDestinationRef(i, messageDestinationRef);
    }

    public MessageDestinationRef[] getGeronimoMessageDestinationRef() {
        return (MessageDestinationRef[]) super.getMessageDestinationRef();
    }

    public void setGeronimoMessageDestinationRef(MessageDestinationRef[] messageDestinationRef) {
        super.setMessageDestinationRef(messageDestinationRef);
    }

    public void setResourceEnvRef(org.apache.geronimo.deployment.model.j2ee.ResourceEnvRef[] resourceEnvRef) {
        assert (resourceEnvRef instanceof ResourceEnvRef[]);
        super.setResourceEnvRef(resourceEnvRef);
    }

    public void setResourceEnvRef(int i, org.apache.geronimo.deployment.model.j2ee.ResourceEnvRef ref) {
        assert (ref instanceof ResourceEnvRef);
        super.setResourceEnvRef(i, ref);
    }

    public ResourceEnvRef getGeronimoResourceEnvRef(int i) {
        return (ResourceEnvRef) getResourceEnvRef(i);
    }

    public void setGeronimoResourceEnvRef(int i, ResourceEnvRef resourceEnvRef) {
        super.setResourceEnvRef(i, resourceEnvRef);
    }

    public ResourceEnvRef[] getGeronimoResourceEnvRef() {
        return (ResourceEnvRef[]) getResourceEnvRef();
    }

    public void setGeronimoResourceEnvRef(ResourceEnvRef[] resourceEnvRef) {
        super.setResourceEnvRef(resourceEnvRef);
    }

    public void setResourceRef(org.apache.geronimo.deployment.model.j2ee.ResourceRef[] resourceRef) {
        assert (resourceRef instanceof ResourceRef[]);
        super.setResourceRef(resourceRef);
    }

    public void setResourceRef(int i, org.apache.geronimo.deployment.model.j2ee.ResourceRef ref) {
        assert (ref instanceof ResourceRef);
        super.setResourceRef(i, ref);
    }

    public ResourceRef getGeronimoResourceRef(int i) {
        return (ResourceRef) getResourceRef(i);
    }

    public void setGeronimoResourceRef(int i, ResourceRef resourceRef) {
        super.setResourceRef(i, resourceRef);
    }

    public ResourceRef[] getGeronimoResourceRef() {
        return (ResourceRef[]) getResourceRef();
    }

    public void setGeronimoResourceRef(ResourceRef[] resourceRef) {
        super.setResourceRef(resourceRef);
    }

    public void setServiceRef(org.apache.geronimo.deployment.model.j2ee.ServiceRef[] serviceRef) {
        assert (serviceRef instanceof ServiceRef[]);
        super.setServiceRef(serviceRef);
    }

    public void setServiceRef(int i, org.apache.geronimo.deployment.model.j2ee.ServiceRef ref) {
        assert (ref instanceof ServiceRef);
        super.setServiceRef(i, ref);
    }

    public ServiceRef[] getGeronimoServiceRef() {
        return (ServiceRef[]) getServiceRef();
    }

    public ServiceRef getGeronimoServiceRef(int i) {
        return (ServiceRef)getServiceRef(i);
    }

    public void setGeronimoServiceRef(ServiceRef[] serviceRef) {
        super.setServiceRef(serviceRef);
    }

    public void setGeronimoServiceRef(int i, ServiceRef ref) {
        super.setServiceRef(i, ref);
    }
}
