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
 * @version $Id: src/main/java/org/freehep/maven/nar/NarDownloadMojo.java 11653eea15a5 2006/06/22 00:03:37 duns $
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
        if (classifiers == null) {
            downloadAttachedNars(null);
        } else {
            for (Iterator j = classifiers.iterator(); j.hasNext();) {
                downloadAttachedNars((String) j.next());
            }
        }
    }

    void downloadAttachedNars(String classifier) throws MojoExecutionException,
            MojoFailureException {
        // FIXME this may not be the right way to do this.... -U ignored and
        // also SNAPSHOT not used

        // FIXME, hardcoded
        String[] types = { "jni", "shared" };

        for (int t = 0; t < types.length; t++) {
            List dependencies = getAttachedNarDependencies("compile",
                    classifier, types[t]);
            for (Iterator i = dependencies.iterator(); i.hasNext();) {
                Artifact dependency = (Artifact) i.next();
                try {
                    System.err.println("Resolving " + dependency);
                    artifactResolver.resolve(dependency,
                            remoteArtifactRepositories, getLocalRepository());
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
}
