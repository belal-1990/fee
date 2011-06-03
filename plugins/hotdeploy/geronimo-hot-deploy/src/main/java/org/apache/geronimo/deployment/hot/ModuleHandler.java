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
package org.apache.geronimo.deployment.hot;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.TargetModuleID;
import javax.enterprise.deploy.spi.exceptions.DeploymentManagerCreationException;
import javax.enterprise.deploy.spi.factories.DeploymentFactory;
import javax.enterprise.deploy.spi.status.ProgressObject;

import org.apache.geronimo.common.DeploymentException;
import org.apache.geronimo.deployment.cli.DeployUtils;
import org.apache.geronimo.deployment.plugin.factories.DeploymentFactoryWithKernel;
import org.apache.geronimo.deployment.plugin.jmx.JMXDeploymentManager;
import org.apache.geronimo.kernel.Kernel;
import org.apache.geronimo.kernel.config.Configuration;
import org.apache.geronimo.kernel.config.ConfigurationManager;
import org.apache.geronimo.kernel.repository.Artifact;
import org.apache.geronimo.kernel.repository.MissingDependencyException;
import org.apache.geronimo.kernel.util.JarUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModuleHandler implements DirectoryMonitor.MonitorListener {

    private static final Logger log = LoggerFactory.getLogger(ModuleHandler.class);
    
    // Try to make this stand out as the user is likely to get a ton of errors if this comes up
    private static final String BAD_LAYOUT_MESSAGE = "CANNOT DEPLOY: It looks like you unpacked an application or module " +
            "directly into the hot deployment directory.  THIS DOES NOT WORK.  You need to unpack into a " +
            "subdirectory directly under the hot deploy directory.  For example, if the hot deploy directory " +
            "is 'deploy/' and your file is 'webapp.war' then you could unpack it into a directory 'deploy/webapp.war/'";
    
    private final DeploymentFactory factory;
    private final ConfigurationManager configManager;
    private String deploymentURI = "deployer:geronimo:inVM";
    private String deploymentUser;
    private String deploymentPassword;
    private volatile String workingOnConfigId;
    
    public ModuleHandler(Kernel kernel, ConfigurationManager configManager) {
        this.factory = new DeploymentFactoryWithKernel(kernel);
        this.configManager = configManager;
    }
    
    public String getDeploymentUser() {
        return deploymentUser;
    }

    public void setDeploymentUser(String deploymentUser) {
        this.deploymentUser = deploymentUser;
    }

    public String getDeploymentPassword() {
        return deploymentPassword;
    }

    public void setDeploymentPassword(String deploymentPassword) {
        this.deploymentPassword = deploymentPassword;
    }
    
    public String getDeploymentURI() {
        return deploymentURI;
    }

    public void setDeploymentURI(String deploymentURI) {
        if (deploymentURI != null && !deploymentURI.trim().equals("")) {
            this.deploymentURI = deploymentURI.trim();
        }
    }
    
    public String getWorkingOnConfigId() {
        return workingOnConfigId;
    }
    
    private DeploymentManager getDeploymentManager() throws DeploymentManagerCreationException {
        DeploymentManager manager = factory.getDeploymentManager(deploymentURI, deploymentUser, deploymentPassword);
        if (manager instanceof JMXDeploymentManager) {
            ((JMXDeploymentManager) manager).setLogConfiguration(false, true);
        }
        return manager;
    }
    
    @Override
    public void scanComplete(Collection<DirectoryMonitor.FileInfo> addedFiles, Collection<DirectoryMonitor.FileInfo> modifiedFiles, Collection<DirectoryMonitor.FileInfo> deletedFiles) {       
        for (DirectoryMonitor.FileInfo deletedFile : deletedFiles) {
            String moduleId = getModuleId(deletedFile);
            if (isModuleDeployed(moduleId)) {
                // check if deployed module is older
                if (deletedFile.getModified() < getModuleLastModified(moduleId)) {
                    fileRemoved(new File(deletedFile.getPath()), moduleId);
                } else {
                    log.debug("File {} was removed but {} module was not undeployed since the deployed module is newer", deletedFile.getPath(), moduleId);
                }
            }
        }

        for (DirectoryMonitor.FileInfo addedFile : addedFiles) {
            String moduleId = getModuleId(addedFile);
            File moduleFile = new File(addedFile.getPath());
            if (isModuleDeployed(moduleId)) {
                // check if deployed module is newer
                if (addedFile.getModified() > getModuleLastModified(moduleId)) {
                    String newModuleId = fileUpdated(moduleFile, moduleId);
                    if (newModuleId != null) {
                        addedFile.setConfigId(newModuleId.length() == 0 ? moduleId : newModuleId);
                    }
                } else {
                    log.debug("File {} was added but {} module was not redeployed since the deployed module is newer", addedFile.getPath(), moduleId);                    
                }
            } else {
                String newModuleId = fileAdded(moduleFile);
                if (newModuleId != null) {
                    addedFile.setConfigId(newModuleId.length() == 0 ? moduleId : newModuleId);
                }
            }
        }

        for (DirectoryMonitor.FileInfo modifiedFile : modifiedFiles) {
            String moduleId = getModuleId(modifiedFile);
            File moduleFile = new File(modifiedFile.getPath());
            if (isModuleDeployed(moduleId)) {
                // check if deployed module is newer
                if (modifiedFile.getModified() > getModuleLastModified(moduleId)) {
                    String newModuleId = fileUpdated(moduleFile, moduleId);
                    if (newModuleId != null) {
                        modifiedFile.setConfigId(newModuleId.length() == 0 ? moduleId : newModuleId);
                    }
                } else {
                    log.debug("File {} was modified but {} module was not redeployed since the deployed module is newer", modifiedFile.getPath(), moduleId);                                    
                }
            } else {
                String newModuleId = fileAdded(moduleFile);
                if (newModuleId != null) {
                    modifiedFile.setConfigId(newModuleId.length() == 0 ? moduleId : newModuleId);
                }
            }
        }
    }

    private String getModuleId(DirectoryMonitor.FileInfo fileInfo) {
        String moduleId = fileInfo.getConfigId();
        if (moduleId == null) {
            moduleId = calculateModuleId(new File(fileInfo.getPath()));
        }
        return moduleId;
    }
    
    private String calculateModuleId(File module) {
        String moduleId = null;
        try {
            moduleId = DeployUtils.extractModuleIdFromArchive(module);
        } catch (Exception e) {
            try {
                moduleId = DeployUtils.extractModuleIdFromPlan(module);
            } catch (Exception e2) {
                log.debug("Unable to calculate module ID for file " + module.getAbsolutePath() + " [" + e2.getMessage() + "]");
            }
        }
        if (moduleId == null) {
            int pos = module.getName().lastIndexOf('.');
            moduleId = pos > -1 ? module.getName().substring(0, pos) : module.getName();
            moduleId = getModuleId(moduleId);
        }
        return moduleId;
    }

    public String getModuleId(String config) {
        DeploymentManager mgr = null;
        try {
            mgr = getDeploymentManager();
            Target[] targets = mgr.getTargets();
            TargetModuleID[] ids = mgr.getAvailableModules(null, targets);
            for (int j = 0; j < ids.length; j++) {
                String moduleId = ids[j].getModuleID();
                String[] parts = moduleId.split("/", -1);
                if (parts.length != 4) {
                    continue;
                }
                if (parts[1] != null && parts[1].equals(config))
                    return ids[j].getModuleID();
            }
        } catch (Exception ex) {
            log.error("Unable to getModuleId", ex);
        } finally {
            if (mgr != null) {
                mgr.release();
            }
        }
        return config;
    }

    public boolean isModuleDeployed(String configId) {
        DeploymentManager mgr = null;
        try {
            mgr = getDeploymentManager();
            Target[] targets = mgr.getTargets();
            TargetModuleID[] ids = mgr.getAvailableModules(null, targets);
            DeployUtils.identifyTargetModuleIDs(ids, configId, true).toArray(new TargetModuleID[0]);
            return true;
        } catch (DeploymentException e) {
            log.debug("Found new file in deploy directory on startup with ID " + configId);
        } catch (Exception e) {
            log.error("Unable to check status", e);
        } finally {
            if (mgr != null) {
                mgr.release();
            }
        }
        return false;
    }

    public String fileAdded(File file) {
        log.info("Deploying " + file.getName());
        DeploymentManager mgr = null;
        TargetModuleID[] modules = null;
        boolean completed = false;
        try {
            mgr = getDeploymentManager();
            Target[] targets = mgr.getTargets();
            if (null == targets) {
                throw new IllegalStateException("No target to distribute to");
            }
            targets = new Target[] { targets[0] };

            ProgressObject po;

            if (JarUtils.isJarFile(file) || file.isDirectory()) {
                po = mgr.distribute(targets, file, null);
            } else {
                po = mgr.distribute(targets, null, file);
            }
            waitForProgress(po);
            if (po.getDeploymentStatus().isCompleted()) {
                modules = po.getResultTargetModuleIDs();
                po = mgr.start(modules);
                waitForProgress(po);
                if (po.getDeploymentStatus().isCompleted()) {
                    completed = true;
                } else {
                    log.warn("Unable to start some modules for " + file.getAbsolutePath());
                }
                modules = po.getResultTargetModuleIDs();
                for (int i = 0; i < modules.length; i++) {
                    TargetModuleID result = modules[i];
                    log.info(DeployUtils.reformat("Deployed " + result.getModuleID()
                            + (targets.length > 1 ? " to " + result.getTarget().getName() : "")
                            + (result.getWebURL() == null ? "" : " @ " + result.getWebURL()), 4, 72));
                    if (result.getChildTargetModuleID() != null) {
                        for (int j = 0; j < result.getChildTargetModuleID().length; j++) {
                            TargetModuleID child = result.getChildTargetModuleID()[j];
                            log.info(DeployUtils.reformat("  `-> " + child.getModuleID()
                                    + (child.getWebURL() == null ? "" : " @ " + child.getWebURL()), 4, 72));
                        }
                    }
                }
            } else {
                log.error("Unable to deploy: " + po.getDeploymentStatus().getMessage());
                return null;
            }
        } catch (DeploymentManagerCreationException e) {
            log.error("Unable to open deployer", e);
        } catch (IOException e) {
            log.error("Unable to determine if file is a jar", e);
        } finally {
            if (mgr != null)
                mgr.release();
        }
        if (completed && modules != null) {
            if (modules.length == 1) {
                return modules[0].getModuleID();
            } else {
                return "";
            }
        } else if (modules != null) { // distribute completed but not start or
                                      // something like that
            return "";
        } else {
            return null;
        }
    }

    public String fileUpdated(File file, String configId) {
        workingOnConfigId = configId;
        log.info("Redeploying " + file.getName());
        DeploymentManager mgr = null;
        TargetModuleID[] modules = null;
        try {
            mgr = getDeploymentManager();
            Target[] targets = mgr.getTargets();
            TargetModuleID[] ids = mgr.getAvailableModules(null, targets);
            ids = DeployUtils.identifyTargetModuleIDs(ids, configId, true).toArray(new TargetModuleID[0]);
            ProgressObject po;
            if (JarUtils.isJarFile(file) || file.isDirectory()) {
                po = mgr.redeploy(ids, file, null);
            } else {
                po = mgr.redeploy(ids, null, file);
            }
            waitForProgress(po);
            if (po.getDeploymentStatus().isCompleted()) {
                modules = po.getResultTargetModuleIDs();
                for (int i = 0; i < modules.length; i++) {
                    TargetModuleID result = modules[i];
                    log.info(DeployUtils.reformat("Redeployed " + result.getModuleID()
                            + (targets.length > 1 ? " to " + result.getTarget().getName() : "")
                            + (result.getWebURL() == null ? "" : " @ " + result.getWebURL()), 4, 72));
                    if (result.getChildTargetModuleID() != null) {
                        for (int j = 0; j < result.getChildTargetModuleID().length; j++) {
                            TargetModuleID child = result.getChildTargetModuleID()[j];
                            log.info(DeployUtils.reformat("  `-> " + child.getModuleID()
                                    + (child.getWebURL() == null ? "" : " @ " + child.getWebURL()), 4, 72));
                        }
                    }
                }
            } else {
                log.error("Unable to redeploy " + file.getAbsolutePath() + "(" + configId + ")"
                        + po.getDeploymentStatus().getMessage());
            }
        } catch (DeploymentManagerCreationException e) {
            log.error("Unable to open deployer", e);
        } catch (Exception e) {
            log.error("Unable to undeploy", e);
        } finally {
            if (mgr != null) {
                mgr.release();
            }
            workingOnConfigId = null;
        }
        if (modules != null) {
            if (modules.length == 1) {
                return modules[0].getModuleID();
            } else {
                return "";
            }
        } else {
            return null;
        }
    }

    public boolean fileRemoved(File file, String configId) {
        workingOnConfigId = configId;
        log.info("Undeploying " + file.getName());
        DeploymentManager mgr = null;
        try {
            mgr = getDeploymentManager();
            Target[] targets = mgr.getTargets();
            TargetModuleID[] ids = mgr.getAvailableModules(null, targets);
            ids = DeployUtils.identifyTargetModuleIDs(ids, configId, true).toArray(new TargetModuleID[0]);
            ProgressObject po = mgr.undeploy(ids);
            waitForProgress(po);
            if (po.getDeploymentStatus().isCompleted()) {
                TargetModuleID[] modules = po.getResultTargetModuleIDs();
                for (int i = 0; i < modules.length; i++) {
                    TargetModuleID result = modules[i];
                    log.info(DeployUtils.reformat("Undeployed " + result.getModuleID()
                            + (targets.length > 1 ? " to " + result.getTarget().getName() : ""), 4, 72));
                }
            } else {
                log.error("Unable to undeploy " + file.getAbsolutePath() + "(" + configId + ")"
                        + po.getDeploymentStatus().getMessage());
                return false;
            }
        } catch (DeploymentManagerCreationException e) {
            log.error("Unable to open deployer", e);
            return false;
        } catch (Exception e) {
            log.error("Unable to undeploy", e);
            return false;
        } finally {
            if (mgr != null) {
                mgr.release();
            }
            workingOnConfigId = null;
        }
        return true;
    }

    private void waitForProgress(ProgressObject po) {
        while (po.getDeploymentStatus().isRunning()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public long getModuleLastModified(String configId) {
        try {
            Artifact art = configManager.getArtifactResolver().resolveInClassLoader(Artifact.create(configId));
            Configuration config = configManager.getConfiguration(art);
            File file = config.getConfigurationResolver().resolve(art);
            long lastModified = DirectoryMonitor.getLastModified(file);
            return lastModified;
        } catch (MissingDependencyException e) {
            log.error("Unknown configuration " + configId);
            return -1;
        }
    }
    
    @Override
    public boolean validateFile(File file, String configId) {
        if (file.isDirectory() && (file.getName().equals("WEB-INF") || file.getName().equals("META-INF"))) {
            log.error("(" + file.getName() + ") " + BAD_LAYOUT_MESSAGE);
            return false;
        }
        return true;
    }
    
}
