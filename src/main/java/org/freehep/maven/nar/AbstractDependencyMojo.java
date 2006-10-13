// Copyright FreeHEP, 2005-2006.
package org.freehep.maven.nar;

import org.apache.maven.plugin.MojoFailureException;

/**
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: src/main/java/org/freehep/maven/nar/AbstractDependencyMojo.java 8c1595ae1e05 2006/10/13 23:26:37 duns $
 */
public abstract class AbstractDependencyMojo extends AbstractNarMojo {
    
	protected NarManager getNarManager() throws MojoFailureException {
		return new NarManager(getLog(), getLogLevel(), getLocalRepository(), getMavenProject(), getArchitecture(), getOS(), getLinker());
	}
}
