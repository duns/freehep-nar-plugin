// Copyright FreeHEP, 2006.
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
 * @description Assemble libraries of NAR files.
 * @goal nar-assembly
 * @phase process-resources
 * @requiresProject
 * @requiresDependencyResolution
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: src/main/java/org/freehep/maven/nar/NarAssemblyMojo.java f306842a5f50 2006/06/21 20:44:59 duns $
 */
public class NarAssemblyMojo extends AbstractDependencyMojo {

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
     * List of classifiers which you want to assemble. Example ppc-MacOSX-g++, x86-Windows-msvc, i386-Linux-g++.
     * 
     *  @parameter expression=""
     *  @required
     */
    private List classifiers;
    
	public void execute() throws MojoExecutionException, MojoFailureException {
        for (Iterator j = classifiers.iterator(); j.hasNext(); ) {
            String classifier = (String)j.next();
            System.err.println("For "+classifier);
            List dependencies = getAttachedNarDependencies("compile", classifier);
            for (Iterator i = dependencies.iterator(); i.hasNext();) {
                Artifact dependency = (Artifact) i.next();
//			try {
                    System.err.println("Assemble from "+dependency);
//				artifactResolver.resolve(dependency, remoteArtifactRepositories, getLocalRepository());	
/*			} catch (ArtifactNotFoundException e) {
                String message = "nar not found " + dependency.getId();
                throw new MojoExecutionException(message, e);
			} catch (ArtifactResolutionException e) {
				String message = "nar cannot resolve " + dependency.getId();
				throw new MojoExecutionException(message, e);
			}
*/		    }
        }
	}	
}
