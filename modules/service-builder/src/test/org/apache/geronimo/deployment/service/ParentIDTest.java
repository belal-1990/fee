/**
 *
 * Copyright 2005 The Apache Software Foundation
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
package org.apache.geronimo.deployment.service;

import java.net.URI;
import java.util.List;
import java.util.LinkedHashSet;

import junit.framework.TestCase;
import org.apache.geronimo.deployment.xbeans.ArtifactType;
import org.apache.geronimo.deployment.xbeans.ArtifactType;
import org.apache.geronimo.kernel.repository.Artifact;

/**
 * @version $Rev$ $Date$
 */
public class ParentIDTest extends TestCase {

    public void testNoParents() throws Exception {
        LinkedHashSet parentId = EnvironmentBuilder.toArtifacts(new ArtifactType[] {});
        assertEquals(0, parentId.size());
    }

    public void testImportParent1() throws Exception {
        ArtifactType anImport = ArtifactType.Factory.newInstance();
        anImport.setGroupId("groupId");
        anImport.setType("type");
        anImport.setArtifactId("artifactId");
        anImport.setVersion("version");
        LinkedHashSet parentId = EnvironmentBuilder.toArtifacts(new ArtifactType[] {anImport});
        assertEquals(1, parentId.size());
        assertEquals("groupId/artifactId/version/type", ((Artifact)parentId.iterator().next()).toURI().getPath());
    }

}
