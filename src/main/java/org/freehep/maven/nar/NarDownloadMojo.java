// Copyright FreeHEP, 2005-2006.
package org.freehep.maven.nar;

import java.util.Iterator;
import java.util.List;

import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @description Download NAR files.
 * @goal nar-download
 * @phase generate-sources
 * @requiresProject
 * @requiresDependencyResolution
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: src/main/java/org/freehep/maven/nar/NarDownloadMojo.java 417210bb60fa 2006/09/27 23:02:41 duns $
 */
public class NarDownloadMojo extends AbstractDependencyMojo {

	/**
	 * Artifact resolver, needed to download source jars for inclusion in
	 * classpath.
	 * 
	 * @component role="org.apache.maven.artifact.resolver.ArtifactResolver"
	 * @required
	 * @readonly
	 */
	private ArtifactResolver artifactResolver;

	/**
	 * Remote repositories which will be searched for source attachments.
	 * 
	 * @parameter expression="${project.remoteArtifactRepositories}"
	 * @required
	 * @readonly
	 */
	private List remoteArtifactRepositories;

	/**
	 * List of classifiers which you want download. Example ppc-MacOSX-g++,
	 * x86-Windows-msvc, i386-Linux-g++.
	 * 
	 * @parameter expression=""
	 */
	private List classifiers;

	public void execute() throws MojoExecutionException, MojoFailureException {
		List narArtifacts = getNarManager().getNarDependencies("compile");
		if (classifiers == null) {
			getNarManager().downloadAttachedNars(narArtifacts, remoteArtifactRepositories,
					artifactResolver, null);
		} else {
			for (Iterator j = classifiers.iterator(); j.hasNext();) {
				getNarManager().downloadAttachedNars(narArtifacts, remoteArtifactRepositories,
						artifactResolver, (String) j.next());
			}
		}
	}
}
