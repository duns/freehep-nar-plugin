// Copyright FreeHEP, 2007.
package org.freehep.maven.nar;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;

/**
 * Copies any resources, including AOL specific distributions, to the target
 * area for packaging
 * 
 * @goal nar-resources
 * @phase process-resources
 * @requiresProject
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: plugin/src/main/java/org/freehep/maven/nar/NarResourcesMojo.java 113f3bde20c0 2007/07/10 19:56:39 duns $
 */
public class NarResourcesMojo extends AbstractNarMojo {

	/**
	 * Directory for nar resources. Defaults to src/nar/resources
	 * 
	 * @parameter expression="${basedir}/src/nar/resources"
	 */
	private File resourceDirectory;

	public void execute() throws MojoExecutionException, MojoFailureException {
		if (shouldSkip())
			return;

		// scan for AOLs
		File aolDir = new File(resourceDirectory, "aol");
		if (aolDir.exists()) {
			String[] aols = aolDir.list();
			for (int i = 0; i < aols.length; i++) {
				copyResources(new File(aolDir, aols[i]), aols[i]);
			}
		}
	}

	private void copyResources(File aolDir, String aol) throws MojoExecutionException {
		getLog().info("Copying resources for " + aol);
		try {
			// include
			File includeDir = new File(aolDir, "include");
			if (includeDir.exists()) {
				File includeDstDir = new File(getTargetDirectory(), "include");
				// FIXME do not include .svn files
				FileUtils.copyDirectoryStructure(includeDir, includeDstDir);
			}
			
			File libDir = new File(aolDir, "lib");
			if (libDir.exists()) {
				File libDstDir = new File(getTargetDirectory(), "lib");
				libDstDir = new File(libDstDir, aol);
				libDstDir = new File(libDstDir, "static");
				// FIXME filter files.
				FileUtils.copyDirectoryStructure(libDir, libDstDir);
			}
		} catch (IOException e) {
			throw new MojoExecutionException("NAR: Could not copy resources", e);
		}
	}

}
