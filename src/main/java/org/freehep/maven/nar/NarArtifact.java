// Copyright 2006, FreeHEP.
package org.freehep.maven.nar;

import java.util.Properties;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;

/**
 * 
 * @author duns
 * @version $Id: src/main/java/org/freehep/maven/nar/NarArtifact.java 73902c059881 2006/06/13 23:38:16 duns $
 */
public class NarArtifact extends DefaultArtifact {

    private Properties narProperties;

    public NarArtifact(Artifact dependency, Properties narProperties) {
        super(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersionRange(), 
              dependency.getScope(), dependency.getType(), dependency.getClassifier(), 
              dependency.getArtifactHandler(), dependency.isOptional());
        this.narProperties = narProperties;
    }
    
    public Properties getProperties() {
        return narProperties;
    }
}
