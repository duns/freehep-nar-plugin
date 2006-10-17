// Copyright FreeHEP, 2005-2006.
package org.freehep.maven.nar;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: src/main/java/org/freehep/maven/nar/AbstractDependencyMojo.java 2bfc7ab24863 2006/10/17 00:24:06 duns $
 */
public abstract class AbstractDependencyMojo extends AbstractNarMojo {

    /**
     * @parameter expression="${localRepository}"
     * @required
     * @readonly
     */
    private ArtifactRepository localRepository;

    protected ArtifactRepository getLocalRepository() {
        return localRepository;
    }
	
	protected NarManager getNarManager() throws MojoFailureException {
		return new NarManager(getLog(), getLogLevel(), getLocalRepository(), getMavenProject(), getArchitecture(), getOS(), getLinker());
	}
}
