// Copyright 2005-2006, FreeHEP.
package org.freehep.maven.nar;
 
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;

/**
 * NarArtifact with its own type, classifier and artifactHandler.
 *
 * @author Mark Donszelmann
 * @version $Id: src/main/java/org/freehep/maven/nar/AttachedNarArtifact.java 83229295dbc0 2006/06/13 18:24:24 duns $
 */
public class AttachedNarArtifact extends DefaultArtifact {

    public AttachedNarArtifact(String groupId, String artifactId, String version, String scope, 
                       String type, String classifier, boolean optional) throws InvalidVersionSpecificationException {
        super(groupId, artifactId, VersionRange.createFromVersionSpec(version), scope, 
              type, classifier, new Handler(classifier), optional);              
    }

    public AttachedNarArtifact(Artifact parent, String type, String classifier) {
        super(parent.getGroupId(), parent.getArtifactId(), parent.getVersionRange(), parent.getScope(), 
              type, classifier, new Handler(classifier), parent.isOptional());
    }
     
    private static class Handler implements ArtifactHandler {
        private String classifier;
        
        Handler(String classifier) {
            this.classifier = classifier;
        }
        
        public String getExtension() {
            return "nar";
        }

        public String getDirectory() {
            return "nars";
        }

        public String getClassifier() {
            return classifier;
        }

        public String getPackaging() {
            return "nar";
        }

        public boolean isIncludesDependencies() {
            return false;
        }

        public String getLanguage() {
            return "native";
        }

        public boolean isAddedToClasspath() {
            return false;
        }
    }
}
