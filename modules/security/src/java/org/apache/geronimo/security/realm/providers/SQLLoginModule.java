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

package org.apache.geronimo.security.realm.providers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.apache.geronimo.security.realm.GenericSecurityRealm;


/**
 * A login module that loads security information from a SQL database.  Expects
 * to be run by a GenericSecurityRealm (doesn't work on its own).
 *
 * @version $Rev$ $Date$
 */
public class SQLLoginModule implements LoginModule {
    public final static String USER_SELECT = "userSelect";
    public final static String GROUP_SELECT = "groupSelect";
    public final static String CONNECTION_URL = "jdbcURL";
    public final static String USER = "jdbcUser";
    public final static String PASSWORD = "jdbcPassword";
    public final static String DRIVER = "jdbcDriver";
    //todo: support JNDI data sources too
    private String connectionURL;
    private Properties properties;
    private Driver driver;
    private String userSelect;
    private String groupSelect;

    private Subject subject;
    private CallbackHandler handler;
    private String cbUsername;
    private String cbPassword;
    private final Set groups = new HashSet();

    public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {
        this.subject = subject;
        this.handler = callbackHandler;

        connectionURL = (String) options.get(CONNECTION_URL);
        properties = new Properties();
        properties.put("user", options.get(USER));
        properties.put("password", options.get(PASSWORD));
        userSelect = (String) options.get(USER_SELECT);
        groupSelect = (String) options.get(GROUP_SELECT);
        ClassLoader cl = (ClassLoader) options.get(GenericSecurityRealm.CLASSLOADER_LM_OPTION);
        try {
            this.driver = (Driver) cl.loadClass((String) options.get(DRIVER)).newInstance();
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Driver class "+driver+" is not available.  Perhaps you need to add it as a dependency in your deployment plan?");
        } catch(Exception e) {
            throw new IllegalArgumentException("Unable to load, instantiate, register driver "+driver+": "+e.getMessage());
        }
    }

    public boolean login() throws LoginException {
        Callback[] callbacks = new Callback[2];

        callbacks[0] = new NameCallback("User name");
        callbacks[1] = new PasswordCallback("Password", false);
        try {
            handler.handle(callbacks);
        } catch (IOException ioe) {
            throw (LoginException) new LoginException().initCause(ioe);
        } catch (UnsupportedCallbackException uce) {
            throw (LoginException) new LoginException().initCause(uce);
        }
        assert callbacks.length == 2;
        cbUsername = ((NameCallback) callbacks[0]).getName();
        if(cbUsername == null || cbUsername.equals("")) {
            return false;
        }
        cbPassword = new String(((PasswordCallback) callbacks[1]).getPassword());

        boolean found = false;
        try {
            Connection conn = driver.connect(connectionURL, properties);

            try {
                PreparedStatement statement = conn.prepareStatement(userSelect);
                try {
                    ResultSet result = statement.executeQuery();

                    try {
                        while (result.next()) {
                            String userName = result.getString(1);
                            String userPassword = result.getString(2);

                            if (cbUsername.equals(userName) && cbPassword.equals(userPassword)) {
                                found = true;
                                break;
                            }
                        }
                    } finally {
                        result.close();
                    }
                } finally {
                    statement.close();
                }

                if (!found) return false;

                statement = conn.prepareStatement(groupSelect);
                try {
                    ResultSet result = statement.executeQuery();

                    try {
                        while (result.next()) {
                            String groupName = result.getString(1);
                            String userName = result.getString(2);

                            if (cbUsername.equals(userName)) {
                                groups.add(new GeronimoGroupPrincipal(groupName));
                            }
                        }
                    } finally {
                        result.close();
                    }
                } finally {
                    statement.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException sqle) {
            throw (LoginException) new LoginException("SQL error").initCause(sqle);
        }

        return true;
    }

    public boolean commit() throws LoginException {
        Set principals = subject.getPrincipals();
        principals.add(new GeronimoUserPrincipal(cbUsername));
        Iterator iter = groups.iterator();
        while (iter.hasNext()) {
            principals.add(iter.next());
        }

        return true;
    }

    public boolean abort() throws LoginException {
        cbUsername = null;
        cbPassword = null;

        return true;
    }

    public boolean logout() throws LoginException {
        cbUsername = null;
        cbPassword = null;

        return true;
    }
}
