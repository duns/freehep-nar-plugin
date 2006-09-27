// Copyright FreeHEP, 2005-2006.
package org.freehep.maven.nar;

import org.apache.maven.plugin.MojoFailureException;

/**
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: src/main/java/org/freehep/maven/nar/AbstractDependencyMojo.java 417210bb60fa 2006/09/27 23:02:41 duns $
 */
public abstract class AbstractDependencyMojo extends AbstractNarMojo {
    
	protected NarManager getNarManager() throws MojoFailureException {
		return new NarManager(getLog(), getLocalRepository(), getMavenProject(), getArchitecture(), getOS(), getLinker());
	}
}
