// Copyright FreeHEP, 2005-2006.
package org.freehep.maven.nar;

import java.util.Iterator;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
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
 * @version $Id: src/main/java/org/freehep/maven/nar/NarDownloadMojo.java fb2f54cb3103 2006/06/08 23:31:35 duns $
 */
public class NarDownloadMojo extends AbstractDependencyMojo {

	/**
	 * Artifact factory, needed to download source jars for inclusion in
	 * classpath.
	 * 
	 * @component role="org.apache.maven.artifact.factory.ArtifactFactory"
	 * @required
	 * @readonly
	 */
//	private ArtifactFactory artifactFactory;

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

	public void execute() throws MojoExecutionException, MojoFailureException {

		List dependencies = getAllNarDependencies("compile");
		for (Iterator i = dependencies.iterator(); i.hasNext();) {
			Artifact dependency = (Artifact) i.next();

			try {
				artifactResolver.resolve(dependency, remoteArtifactRepositories, localRepository);
			} catch (ArtifactNotFoundException e) {
                String message = "nar not found " + dependency.getId();
                throw new MojoExecutionException(message, e);
			} catch (ArtifactResolutionException e) {
				String message = "nar cannot resolve " + dependency.getId();
				throw new MojoExecutionException(message, e);
			}
		}
	}	
}
