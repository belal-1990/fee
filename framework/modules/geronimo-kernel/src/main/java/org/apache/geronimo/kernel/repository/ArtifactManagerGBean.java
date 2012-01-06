/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.apache.geronimo.kernel.repository;

import java.util.Set;
import java.util.SortedSet;

import org.apache.geronimo.gbean.annotation.GBean;
import org.apache.geronimo.gbean.annotation.ParamSpecial;
import org.apache.geronimo.gbean.annotation.SpecialAttributeType;
import org.apache.geronimo.gbean.wrapper.AbstractServiceWrapper;
import org.osgi.framework.Bundle;

/**
 * @version $Rev:$ $Date:$
 */
@GBean(j2eeType = "ArtifactManager")
public class ArtifactManagerGBean extends AbstractServiceWrapper<ArtifactManager> implements ArtifactManager {
    public ArtifactManagerGBean(@ParamSpecial(type = SpecialAttributeType.bundle)final Bundle bundle) {
        super(bundle, ArtifactManager.class);
    }

    @Override
    public void loadArtifacts(Artifact artifact, Set<Artifact> artifacts) {
        get().loadArtifacts(artifact, artifacts);
    }

    @Override
    public void unloadAllArtifacts(Artifact artifact) {
        get().unloadAllArtifacts(artifact);
    }

    @Override
    public SortedSet<Artifact> getLoadedArtifacts(Artifact query) {
        return get().getLoadedArtifacts(query);
    }
}
