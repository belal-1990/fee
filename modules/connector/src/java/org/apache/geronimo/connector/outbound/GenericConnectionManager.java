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

package org.apache.geronimo.connector.outbound;

import org.apache.geronimo.connector.outbound.connectionmanagerconfig.PartitionedPool;
import org.apache.geronimo.connector.outbound.connectionmanagerconfig.TransactionSupport;
import org.apache.geronimo.connector.outbound.connectionmanagerconfig.XATransactions;
import org.apache.geronimo.connector.outbound.connectionmanagerconfig.PoolingSupport;
import org.apache.geronimo.connector.outbound.connectionmanagerconfig.SinglePool;
import org.apache.geronimo.connector.outbound.connectiontracking.ConnectionTracker;
import org.apache.geronimo.gbean.GBeanInfo;
import org.apache.geronimo.gbean.GBeanInfoFactory;
import org.apache.geronimo.security.bridge.RealmBridge;

/**
 * GenericConnectionManager sets up a connection manager stack according to the
 *  policies described in the attributes.
 *
 * @version $Revision: 1.1 $ $Date: 2004/05/06 03:58:22 $
 * */
public class GenericConnectionManager extends AbstractConnectionManager {

    //connection manager configuration choices
    private TransactionSupport transactionSupport;
    private PoolingSupport pooling;
    /**
     * Identifying string used by unshareable resource detection
     */
    private String name;
    //dependencies
    protected RealmBridge realmBridge;
    protected ConnectionTracker connectionTracker;

    //default constructor for use as endpoint
    public GenericConnectionManager() {
    }

    public GenericConnectionManager(TransactionSupport transactionSupport,
                                    PoolingSupport pooling,
                                    String name,
                                    RealmBridge realmBridge,
                                    ConnectionTracker connectionTracker) {
        this.transactionSupport = transactionSupport;
        this.pooling = pooling;
        this.name = name;
        this.realmBridge = realmBridge;
        this.connectionTracker = connectionTracker;
    }

    /**
     * Order of constructed interceptors:
     *
     * ConnectionTrackingInterceptor (connectionTracker != null)
     * ConnectionHandleInterceptor
     * TransactionCachingInterceptor (useTransactions & useTransactionCaching)
     * TransactionEnlistingInterceptor (useTransactions)
     * SubjectInterceptor (realmBridge != null)
     * SinglePoolConnectionInterceptor or MultiPoolConnectionInterceptor
     * LocalXAResourceInsertionInterceptor or XAResourceInsertionInterceptor (useTransactions (&localTransactions))
     * MCFConnectionInterceptor
     */
    protected void setUpConnectionManager() throws IllegalStateException {
        //check for consistency between attributes
        if (realmBridge == null && pooling instanceof PartitionedPool && ((PartitionedPool)pooling).isPartitionBySubject()) {
            throw new IllegalStateException("To use Subject in pooling, you need a SecurityDomain");
        }

        //Set up the interceptor stack
        MCFConnectionInterceptor tail = new MCFConnectionInterceptor();
        ConnectionInterceptor stack = tail;

        stack = transactionSupport.addXAResourceInsertionInterceptor(stack);
        stack = pooling.addPoolingInterceptors(stack);
        //experimental threadlocal caching
        if (transactionSupport instanceof XATransactions && ((XATransactions)transactionSupport).isUseThreadCaching()) {
            stack = new ThreadLocalCachingConnectionInterceptor(stack, false);
        }
        if (realmBridge != null) {
            stack = new SubjectInterceptor(stack, realmBridge);
        }
        stack = transactionSupport.addTransactionInterceptors(stack);

        stack = new ConnectionHandleInterceptor(stack);
        if (connectionTracker != null) {
            stack = new ConnectionTrackingInterceptor(
                    stack,
                    getName(),
                    connectionTracker,
                    realmBridge);
        }
        tail.setStack(stack);
        this.stack = stack;
    }


    public TransactionSupport getTransactionSupport() {
        return transactionSupport;
    }

    public void setTransactionSupport(TransactionSupport transactionSupport) {
        this.transactionSupport = transactionSupport;
    }

    public PoolingSupport getPooling() {
        return pooling;
    }

    public void setPooling(PoolingSupport pooling) {
        this.pooling = pooling;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RealmBridge getRealmBridge() {
        return realmBridge;
    }

    public void setRealmBridge(RealmBridge realmBridge) {
        this.realmBridge = realmBridge;
    }

    public ConnectionTracker getConnectionTracker() {
        return connectionTracker;
    }

    public void setConnectionTracker(ConnectionTracker connectionTracker) {
        this.connectionTracker = connectionTracker;
    }

    public static final GBeanInfo GBEAN_INFO;

    static {
        GBeanInfoFactory infoFactory = new GBeanInfoFactory(GenericConnectionManager.class.getName(), AbstractConnectionManager.GBEAN_INFO);

        infoFactory.addAttribute("Name", true);
        infoFactory.addAttribute("TransactionSupport", true);
        infoFactory.addAttribute("Pooling", true);

        infoFactory.addReference("ConnectionTracker", ConnectionTracker.class);
        infoFactory.addReference("RealmBridge", RealmBridge.class);

        infoFactory.setConstructor(
                new String[]{"TransactionSupport", "Pooling", "Name", "RealmBridge", "ConnectionTracker"},
                new Class[]{TransactionSupport.class, PoolingSupport.class, String.class, RealmBridge.class, ConnectionTracker.class});
        GBEAN_INFO = infoFactory.getBeanInfo();
    }

    public static GBeanInfo getGBeanInfo() {
        return GenericConnectionManager.GBEAN_INFO;
    }


}
