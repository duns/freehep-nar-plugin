// Copyright FreeHEP, 2007.
package org.freehep.maven.nar;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.SelectorUtils;
import org.codehaus.plexus.util.StringUtils;

/**
 * Copies any resources, including AOL specific distributions, to the target
 * area for packaging
 * 
 * @goal nar-resources
 * @phase process-resources
 * @requiresProject
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: plugin/src/main/java/org/freehep/maven/nar/NarResourcesMojo.java 3ac1d2951571 2007/07/10 21:53:48 duns $
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
				boolean ignore = false;
				for (Iterator j = FileUtils.getDefaultExcludesAsList()
						.iterator(); j.hasNext();) {
					if (SelectorUtils.matchPath((String) j.next(), aols[i])) {
						ignore = true;
						break;
					}
				}
				if (!ignore) {
					getLog().info("" + aols[i]);
					copyResources(new File(aolDir, aols[i]));
				}
			}
		}
	}

	private void copyResources(File aolDir)
			throws MojoExecutionException, MojoFailureException {
		String aol = aolDir.getName();
		getLog().info("Copying resources for " + aol);
		try {
			// copy headers
			File includeDir = new File(aolDir, "include");
			if (includeDir.exists()) {
				File includeDstDir = new File(getTargetDirectory(), "include");
				NarUtil.copyDirectoryStructure(includeDir, includeDstDir, null,
						NarUtil.DEFAULT_EXCLUDES);
			}

			// copy libraries
			File libDir = new File(aolDir, "lib");
			if (libDir.exists()) {
				// FIXME, we need all type of libraries.
				String type = "static";
				File libDstDir = new File(getTargetDirectory(), "lib");
				libDstDir = new File(libDstDir, aol);
				libDstDir = new File(libDstDir, type);
				// FIXME filter files.
				String includes = "**/*."
						+ NarUtil.getDefaults().getProperty(
								NarUtil.getAOLKey(aol) + "." + type
										+ ".extension");
				NarUtil.copyDirectoryStructure(libDir, libDstDir, includes,
						NarUtil.DEFAULT_EXCLUDES);
			}
		} catch (IOException e) {
			throw new MojoExecutionException("NAR: Could not copy resources", e);
		}
	}

}
