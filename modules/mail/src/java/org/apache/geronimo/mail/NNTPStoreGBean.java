/**
 *
 * Copyright 2006 The Apache Software Foundation
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
package org.apache.geronimo.mail;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.geronimo.gbean.GBeanInfo;
import org.apache.geronimo.gbean.GBeanInfoBuilder;

/**
 * A GBean that provides for the configuration of a JavaMail NNTP transport
 * protocol.
 * <p/>
 * NNTP transport properties that are common to all NNTP transports are
 * provided via member variables of this class.  Values that are set in the
 * individual member variables will override any of the corresponding values
 * that have been set in the properties set.
 *
 * @version $Rev$ $Date$
 * @see MailGBean
 */
public class NNTPStoreGBean extends ProtocolGBean {

    private final Log log = LogFactory.getLog(NNTPTransportGBean.class);

    private Integer port;
    private Integer connectionTimeout;
    private Integer timeout;
    private Boolean auth;
    private String saslRealm;
    private Boolean quitWait;
    private String socketFactoryClass;
    private Boolean socketFactoryFallback;
    private Integer socketFactoryPort;


    /**
     * Construct an instance of NNTPStoreGBean
     * <p/>
     * Values that are set in the individual member variables will override any of
     * the corresponding values that have been set in the properties set.
     *
     * @param objectName            the object name of the protocol
     * @param properties            the set of default properties for the protocol
     * @param host                  the host the protocol connects to
     * @param user                  the default name for the protocol
     * @param port                  the NNTP server port
     * @param connectionTimeout     the socket connection timeout value in milliseconds
     * @param timeout               the socket I/O timeout value in milliseconds
     * @param auth                  whether an attempt will be made to authenticate the user
     * @param saslRealm             the realm to use with DIGEST-MD5 authentication
     * @param quitWait              whether the transport will wait for the response to the QUIT command
     * @param socketFactoryClass    the class that will be used to create NNTP sockets
     * @param socketFactoryFallback whether java.net.Socket class will be created if the specified
     *                              socket factory class cannot be created
     * @param socketFactoryPort     whether java.net.Socket class will be created if the specified
     *                              socket factory class cannot be created
     */
    public NNTPStoreGBean(String objectName, Properties properties, String host, String user,
                              Integer port,
                              Integer connectionTimeout,
                              Integer timeout,
                              Boolean auth,
                              String saslRealm,
                              Boolean quitWait,
                              String socketFactoryClass,
                              Boolean socketFactoryFallback,
                              Integer socketFactoryPort) {
        super(objectName, "nntp", properties, host, user);

        setPort(port);
        setConnectionTimeout(connectionTimeout);
        setTimeout(timeout);
        setAuth(auth);
        setSaslRealm(saslRealm);
        setQuitWait(quitWait);
        setSocketFactoryClass(socketFactoryClass);
        setSocketFactoryFallback(socketFactoryFallback);
        setSocketFactoryPort(socketFactoryPort);
    }

    /**
     * Returns the NNTP server port to connect to, if the connect() method
     * doesn't explicitly specify one.
     */
    public Integer getPort() {
        return port;
    }

    /**
     * Sets the NNTP server port to connect to, if the connect() method
     * doesn't explicitly specify one.
     * <p/>
     * Defaults to 25.
     * <p/>
     * Values that are set here will override any of the corresponding value
     * that has been set in the properties.
     *
     * @param port the NNTP server port to connect to
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * Returns the socket connection timeout value in milliseconds.
     */
    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * Sets the socket connection timeout value in milliseconds.
     * <p/>
     * Default is infinite timeout.
     * <p/>
     * Values that are set here will override any of the corresponding value
     * that has been set in the properties.
     *
     * @param connectionTimeout the socket connection timeout value in milliseconds.
     */
    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * Returns the socket I/O timeout value in milliseconds.
     */
    public Integer getTimeout() {
        return timeout;
    }

    /**
     * Sets the socket I/O timeout value in milliseconds.
     * <p/>
     * Default is infinite timeout.
     * <p/>
     * Values that are set here will override any of the corresponding value
     * that has been set in the properties.
     *
     * @param timeout the socket I/O timeout value in milliseconds
     */
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }


    /**
     * Returns whether an attempt will be made to authenticate the user.
     * <p/>
     * Defaults to false.
     */
    public Boolean getAuth() {
        return auth;
    }

    /**
     * Sets whether an attempt will be made to authenticate the user.
     * <p/>
     * Defaults to false.
     * <p/>
     * Values that are set here will override any of the corresponding value
     * that has been set in the properties.
     *
     * @param auth whether an attempt will be made to authenticate the user.
     */
    public void setAuth(Boolean auth) {
        this.auth = auth;
    }

    /**
     * Returns the realm to use with DIGEST-MD5 authentication.
     */
    public String getSaslRealm() {
        return saslRealm;
    }

    /**
     * Sets the realm to use with DIGEST-MD5 authentication.
     * <p/>
     * Values that are set here will override any of the corresponding value
     * that has been set in the properties.
     *
     * @param saslRealm the realm to use with DIGEST-MD5 authentication
     */
    public void setSaslRealm(String saslRealm) {
        this.saslRealm = saslRealm;
    }

    /**
     * Returns whether the transport will wait for the response to the QUIT command.
     * <p/>
     * If set to true, causes the transport to wait for the response to the QUIT
     * command. If set to false (the default), the QUIT command is sent and the
     * connection is immediately closed.
     */
    public Boolean getQuitWait() {
        return quitWait;
    }

    /**
     * Sets whether the transport will wait for the response to the QUIT command
     * <p/>
     * If set to true, causes the transport to wait for the response to the QUIT
     * command. If set to false (the default), the QUIT command is sent and the
     * connection is immediately closed.
     * <p/>
     * Values that are set here will override any of the corresponding value
     * that has been set in the properties.
     *
     * @param quitWait whether the transport will wait for the response to the QUIT command
     */
    public void setQuitWait(Boolean quitWait) {
        this.quitWait = quitWait;
    }

    /**
     * Returns the class that will be used to create NNTP sockets.
     * <p/>
     * If set, specifies the name of a class that implements the
     * javax.net.SocketFactory interface. This class will be used to create NNTP
     * sockets.
     */
    public String getSocketFactoryClass() {
        return socketFactoryClass;
    }

    /**
     * Sets the class that will be used to create NNTP sockets.
     * <p/>
     * If set, specifies the name of a class that implements the
     * javax.net.SocketFactory interface. This class will be used to create NNTP
     * sockets.
     * <p/>
     * Values that are set here will override any of the corresponding value
     * that has been set in the properties.
     *
     * @param socketFactoryClass the class that will be used to create NNTP sockets
     */
    public void setSocketFactoryClass(String socketFactoryClass) {
        this.socketFactoryClass = socketFactoryClass;
    }

    /**
     * Returns whether java.net.Socket class will be created if the specified
     * socket factory class cannot be created.
     * <p/>
     * If set to true, failure to create a socket using the specified socket
     * factory class will cause the socket to be created using the
     * java.net.Socket class. Defaults to true.
     */
    public Boolean getSocketFactoryFallback() {
        return socketFactoryFallback;
    }

    /**
     * Sets whether java.net.Socket class will be created if the specified
     * socket factory class cannot be created.
     * <p/>
     * If set to true, failure to create a socket using the specified socket
     * factory class will cause the socket to be created using the
     * java.net.Socket class. Defaults to true.
     * <p/>
     * Values that are set here will override any of the corresponding value
     * that has been set in the properties.
     *
     * @param socketFactoryFallback whether java.net.Socket class will be created if the specified
     *                              socket factory class cannot be created
     */
    public void setSocketFactoryFallback(Boolean socketFactoryFallback) {
        this.socketFactoryFallback = socketFactoryFallback;
    }

    /**
     * Returns the port to connect to when using the specified socket factory.
     * <p/>
     * Specifies the port to connect to when using the specified socket
     * factory. If not set, the default port will be used.
     */
    public Integer getSocketFactoryPort() {
        return socketFactoryPort;
    }

    /**
     * Sets the port to connect to when using the specified socket factory.
     * <p/>
     * Specifies the port to connect to when using the specified socket
     * factory. If not set, the default port will be used.
     * <p/>
     * Values that are set here will override any of the corresponding value
     * that has been set in the properties.
     *
     * @param socketFactoryPort the port to connect to when using the specified socket factory
     */
    public void setSocketFactoryPort(Integer socketFactoryPort) {
        this.socketFactoryPort = socketFactoryPort;
    }

    /**
     * Add the overrides from the member variables to the properties file.
     */
    public void addOverrides(Properties props) {
        super.addOverrides(props);

        if (port != null) props.put("mail.nntp.port", port);
        if (connectionTimeout != null) props.put("mail.nntp.connectiontimeout", connectionTimeout);
        if (timeout != null) props.put("mail.nntp.timeout", timeout);
        if (auth != null) props.put("mail.nntp.auth", auth);
        if (saslRealm != null) props.put("mail.nntp.sasl.realm", saslRealm);
        if (quitWait != null) props.put("mail.nntp.quitwait", quitWait);
        if (socketFactoryClass != null) props.put("mail.nntp.socketFactory.class", socketFactoryClass);
        if (socketFactoryFallback != null) props.put("mail.nntp.socketFactory.fallback", socketFactoryFallback);
        if (socketFactoryPort != null) props.put("mail.nntp.socketFactory.port", socketFactoryPort);
    }

    public void doStart() throws Exception {
        log.debug("Started " + getObjectName());
    }

    public void doStop() throws Exception {
        log.debug("Stopped " + getObjectName());
    }

    public void doFail() {
        log.warn("Failed " + getObjectName());
    }

    public static final GBeanInfo GBEAN_INFO;

    static {
        GBeanInfoBuilder infoFactory = GBeanInfoBuilder.createStatic(NNTPStoreGBean.class);

        infoFactory.addAttribute("port", Integer.class, true);
        infoFactory.addAttribute("connectionTimeout", Integer.class, true);
        infoFactory.addAttribute("timeout", Integer.class, true);
        infoFactory.addAttribute("auth", Boolean.class, true);
        infoFactory.addAttribute("saslRealm", String.class, true);
        infoFactory.addAttribute("quitWait", Boolean.class, true);
        infoFactory.addAttribute("socketFactoryClass", String.class, true);
        infoFactory.addAttribute("socketFactoryFallback", Boolean.class, true);
        infoFactory.addAttribute("socketFactoryPort", Integer.class, true);

        infoFactory.addAttribute("objectName", String.class, false);
        infoFactory.addAttribute("protocol", String.class, false);
        infoFactory.addAttribute("properties", Properties.class, true);
        infoFactory.addAttribute("host", String.class, true);
        infoFactory.addAttribute("user", String.class, true);
        infoFactory.addOperation("addOverrides", new Class[]{Properties.class});

        infoFactory.setConstructor(new String[]{"objectName", "properties", "host", "user",
                                                "port",
                                                "connectionTimeout",
                                                "timeout",
                                                "auth",
                                                "saslRealm",
                                                "quitWait",
                                                "socketFactoryClass",
                                                "socketFactoryFallback",
                                                "socketFactoryPort"});

        GBEAN_INFO = infoFactory.getBeanInfo();
    }

    public static GBeanInfo getGBeanInfo() {
        return GBEAN_INFO;
    }
}


