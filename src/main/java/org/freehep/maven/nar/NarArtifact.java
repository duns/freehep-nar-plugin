// Copyright 2006, FreeHEP.
package org.freehep.maven.nar;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;

/**
 * 
 * @author duns
 * @version $Id: src/main/java/org/freehep/maven/nar/NarArtifact.java d3e5b1ffc9be 2006/06/15 22:00:33 duns $
 */
public class NarArtifact extends DefaultArtifact {

    private NarInfo narInfo;

    public NarArtifact(Artifact dependency, NarInfo narInfo) {
        super(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersionRange(), 
              dependency.getScope(), dependency.getType(), dependency.getClassifier(), 
              dependency.getArtifactHandler(), dependency.isOptional());
        this.narInfo = narInfo;
    }
    
    public NarInfo getNarInfo() {
        return narInfo;
    }
}
