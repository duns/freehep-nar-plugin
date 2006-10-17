// Copyright FreeHEP, 2006.
package org.freehep.maven.nar;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;

/**
 * Assemble libraries of NAR files.
 * 
 * @goal nar-assembly
 * @phase process-resources
 * @requiresProject
 * @requiresDependencyResolution
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: src/main/java/org/freehep/maven/nar/NarAssemblyMojo.java 2bfc7ab24863 2006/10/17 00:24:06 duns $
 */
public class NarAssemblyMojo extends AbstractDependencyMojo {

	/**
	 * List of classifiers which you want to assemble. Example ppc-MacOSX-g++,
	 * x86-Windows-msvc, i386-Linux-g++.
	 * 
	 * @parameter expression=""
	 * @required
	 */
	private List classifiers;

	public void execute() throws MojoExecutionException, MojoFailureException {
		for (Iterator j = classifiers.iterator(); j.hasNext();) {
			String classifier = (String) j.next();
			System.err.println("For " + classifier);

			// FIXME, hardcoded
			String[] types = { "jni", "shared" };

			for (int t = 0; t < types.length; t++) {
				List narArtifacts = getNarManager().getNarDependencies(
						"compile");
				List dependencies = getNarManager().getAttachedNarDependencies(
						narArtifacts, classifier, types[t]);
				for (Iterator i = dependencies.iterator(); i.hasNext();) {
					Artifact dependency = (Artifact) i.next();
					System.err.println("Assemble from " + dependency);

					String prefix = classifier.replace("-", ".") + ".";

					// FIXME reported to maven developer list, isSnapshot
					// changes behaviour
					// of getBaseVersion, called in pathOf.
					if (dependency.isSnapshot())
						;
					File src = new File(getLocalRepository().pathOf(dependency));
					src = new File(getLocalRepository().getBasedir(), src
							.getParent());
					src = new File(src, "nar/lib/"
							+ classifier
							+ "/"
							+ types[t]
							+ "/"
							+ NarUtil.getDefaults().getProperty(
									prefix + "lib.prefix")
							+ dependency.getArtifactId()
							+ "-"
							+ dependency.getVersion()
							+ "."
							+ NarUtil.getDefaults().getProperty(
									prefix + types[t] + ".extension"));
					File dst = new File("target/nar/lib/" + classifier + "/"
							+ types[t]);
					try {
						FileUtils.copyFileToDirectory(src, dst);
					} catch (IOException ioe) {
						System.err.println("WARNING (ignored): Failed to copy "
								+ src + " to " + dst);
						// System.err.println(ioe);
					}
				}
			}
		}
	}
}
