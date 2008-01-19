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
package org.apache.geronimo.deployment.cli;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.enterprise.deploy.spi.DeploymentManager;
import javax.security.auth.login.FailedLoginException;

import jline.ConsoleReader;
import org.apache.geronimo.cli.deployer.CommandArgs;
import org.apache.geronimo.common.DeploymentException;
import org.apache.geronimo.deployment.plugin.GeronimoDeploymentManager;
import org.apache.geronimo.kernel.config.NoSuchStoreException;
import org.apache.geronimo.system.plugin.DownloadResults;
import org.apache.geronimo.system.plugin.PluginInstallerGBean;
import org.apache.geronimo.system.plugin.model.PluginArtifactType;
import org.apache.geronimo.system.plugin.model.PluginListType;
import org.apache.geronimo.system.plugin.model.PluginType;

/**
 * The CLI deployer logic to start.
 *
 * @version $Rev$ $Date$
 */
public class CommandListConfigurations extends AbstractCommand {

    //todo: provide a way to handle a username and password for the remote repo?

    public void execute(ConsoleReader consoleReader, ServerConnection connection, CommandArgs commandArgs) throws DeploymentException {
        DeploymentManager dmgr = connection.getDeploymentManager();
        if (dmgr instanceof GeronimoDeploymentManager) {
            GeronimoDeploymentManager mgr = (GeronimoDeploymentManager) dmgr;
            try {
                String repo;
                if (commandArgs.getArgs().length == 1) {
                    repo = commandArgs.getArgs()[0];
                } else {
                    repo = getRepository(consoleReader, mgr);
                }
                Map<String, Collection<PluginType>> categories = getPluginCategories(repo, mgr, consoleReader);
                if (categories == null) {
                    return;
                }

                PluginListType list = getInstallList(categories, consoleReader, repo);
                if (list == null) {
                    return;
                }

                installPlugins(mgr, list, repo, consoleReader, connection);
            } catch (IOException e) {
                throw new DeploymentException("Unable to install configuration", e);
            } catch (NumberFormatException e) {
                throw new DeploymentException("Invalid response");
            }
        } else {
            throw new DeploymentException("Cannot list repositories when connected to " + connection.getServerURI());
        }
    }

    public String getRepository(ConsoleReader consoleReader, GeronimoDeploymentManager mgr) throws IOException, DeploymentException {
        URL[] all = mgr.getRepositories();
        if (all.length == 0) {
            throw new DeploymentException("No default repositories available.  Please either specify the repository " +
                    "URL on the command line, or go into the console Plugin page and update the list of available " +
                    "repositories.");
        }
        // no need to ask for input if only one repo exists
        if (all.length == 1) {
            String repo = all[0].toString();
            consoleReader.printNewline();
            consoleReader.printString("Selected repository: " + repo);
            consoleReader.printNewline();
            return repo;
        }
        
        consoleReader.printNewline();
        consoleReader.printString("Select repository:");
        consoleReader.printNewline();
        for (int i = 0; i < all.length; i++) {
            URL url = all[i];
            DeployUtils.printTo("  " + (i + 1) + ". ", 8, consoleReader);
            DeployUtils.println(url.toString(), 0, consoleReader);
        }
        String entry = consoleReader.readLine("Enter Repository Number: ").trim();
        if (entry.length() == 0) {
            return null;
        }
        try {
            int index = Integer.parseInt(entry);
            return all[index - 1].toString();
        } catch (NumberFormatException e) {
            throw new DeploymentException("Invalid selection");
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new DeploymentException("Invalid selection");
        }
    }

    public Map<String, Collection<PluginType>> getPluginCategories(String repo, GeronimoDeploymentManager mgr, ConsoleReader consoleReader) throws DeploymentException, IOException {
        if (repo == null) {
            return null;
        }
        PluginListType data;
        URL repository;
        try {
            repository = new URL(repo);
            data = mgr.listPlugins(repository, null, null);
        } catch (IOException e) {
            throw new DeploymentException("Unable to list configurations", e);
        } catch (FailedLoginException e) {
            throw new DeploymentException("Invalid login for Maven repository '" + repo + "'", e);
        }
        return writePluginList(data, consoleReader);
    }

    public Map<String, Collection<PluginType>> getLocalPluginCategories(GeronimoDeploymentManager mgr, ConsoleReader consoleReader) throws DeploymentException, IOException {
        PluginListType data;
        try {
            data = mgr.createPluginListForRepositories(null);
        } catch (NoSuchStoreException e) {
            throw new DeploymentException("Unable to list configurations", e);
        }
        return writePluginList(data, consoleReader);
    }

    private Map<String, Collection<PluginType>> writePluginList(PluginListType data, ConsoleReader consoleReader) throws IOException {
        if (data == null) {
            consoleReader.printNewline();
            consoleReader.printString("No plugins were returned from this site.");
            consoleReader.printNewline();
            consoleReader.flushConsole();
            return null;
        }
        Map<String, Collection<PluginType>> categories = new TreeMap<String, Collection<PluginType>>();
        Comparator<PluginType> comp = new Comparator<PluginType>() {

            public int compare(PluginType o1, PluginType o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };
        for (PluginType metadata : data.getPlugin()) {
            String category = metadata.getCategory();
            if (category == null) {
                category = "<no category>";
            }
            Collection<PluginType> list = categories.get(category);
            if (list == null) {
                list = new TreeSet<PluginType>(comp);
                categories.put(category, list);
            }
            list.add(metadata);
        }
        return categories;
    }

    public PluginListType getInstallList(Map<String, Collection<PluginType>> categories, ConsoleReader consoleReader, String repo) throws IOException {
        List<PluginType> available = new ArrayList<PluginType>();
        for (Map.Entry<String, Collection<PluginType>> entry : categories.entrySet()) {
            String category = entry.getKey();
            Collection<PluginType> items = entry.getValue();
            consoleReader.printString(category);
            consoleReader.printNewline();
            for (PluginType metadata : items) {
                for (PluginArtifactType instance : metadata.getPluginArtifact()) {
                    PluginType copy = PluginInstallerGBean.copy(metadata, instance);
                    available.add(copy);
                    DeployUtils.printTo("  " + available.size() + ":  ", 10, consoleReader);
                    DeployUtils.println(metadata.getName() + " (" + instance.getModuleId().getVersion() + ")", 0, consoleReader);
                }
            }
        }
        if (available.size() == 0) {
            consoleReader.printNewline();
            consoleReader.printString("No plugins from this site are eligible for installation.");
            consoleReader.printNewline();
            return null;
        }
        consoleReader.printNewline();
        consoleReader.flushConsole();
        String answer = consoleReader.readLine("Install Services [enter a comma separated list of numbers or 'q' to quit]: ").trim();
        if (answer.equalsIgnoreCase("q")) {
            return null;
        }
        PluginListType list = new PluginListType();
        for (String instance : answer.split(",")) {
            int selection = Integer.parseInt(instance.trim());
            PluginType target = available.get(selection - 1);
            list.getPlugin().add(target);
        }
        if (repo != null) {
            list.getDefaultRepository().add(repo);
        }
        return list;
    }

    public void installPlugins(GeronimoDeploymentManager mgr, PluginListType list, String defaultRepository, ConsoleReader consoleReader, ServerConnection connection) throws IOException, DeploymentException {
        long start = System.currentTimeMillis();
        Object key = mgr.startInstall(list, defaultRepository, false, null, null);
        DownloadResults results = CommandInstallCAR.showProgress(consoleReader, mgr, key);
        int time = (int) (System.currentTimeMillis() - start) / 1000;
        CommandInstallCAR.printResults(consoleReader, results, time);
    }
    
    public void assembleServer(GeronimoDeploymentManager mgr, PluginListType list, String repositoryPath, String relativeServerPath, ConsoleReader consoleReader) throws Exception {
        long start = System.currentTimeMillis();
        DownloadResults results = mgr.installPluginList(repositoryPath, relativeServerPath, list);
        int time = (int) (System.currentTimeMillis() - start) / 1000;
        CommandInstallCAR.printResults(consoleReader, results, time);
    }

}
