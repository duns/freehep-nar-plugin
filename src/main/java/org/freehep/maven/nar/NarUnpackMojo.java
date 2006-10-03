// Copyright FreeHEP, 2005-2006.
package org.freehep.maven.nar;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;
import org.codehaus.plexus.util.FileUtils;

/**
 * Unpacks NAR files. Unpacking happens in the local repository, 
 * and also sets flags on binaries and corrects static libraries.
 * 
 * @goal nar-unpack
 * @phase process-sources
 * @requiresProject
 * @requiresDependencyResolution
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: src/main/java/org/freehep/maven/nar/NarUnpackMojo.java ef838d8b7f19 2006/10/03 21:41:57 duns $
 */
public class NarUnpackMojo extends AbstractDependencyMojo {

    /**
     * List of classifiers which you want unpack. Example ppc-MacOSX-g++,
     * x86-Windows-msvc, i386-Linux-g++.
     * 
     * @parameter expression=""
     */
    private List classifiers;

    public void execute() throws MojoExecutionException, MojoFailureException {
    	if (shouldSkip()) return;
    	
		List narArtifacts = getNarManager().getNarDependencies("compile");
        if (classifiers == null) {
            getNarManager().unpackAttachedNars(narArtifacts, getArchiverManager(), null, getOS());
        } else {
            for (Iterator j = classifiers.iterator(); j.hasNext();) {
            	getNarManager().unpackAttachedNars(narArtifacts, getArchiverManager(), (String) j.next(), getOS());
            }
        }
    }
}
