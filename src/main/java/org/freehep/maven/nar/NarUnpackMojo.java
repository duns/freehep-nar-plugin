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
 * @description Unpack NAR files.
 * @goal nar-unpack
 * @phase process-sources
 * @requiresProject
 * @requiresDependencyResolution
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: src/main/java/org/freehep/maven/nar/NarUnpackMojo.java 11653eea15a5 2006/06/22 00:03:37 duns $
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
        if (classifiers == null) {
            unpackAttachedNars(null);
        } else {
            for (Iterator j = classifiers.iterator(); j.hasNext();) {
                unpackAttachedNars((String) j.next());
            }
        }
    }

    void unpackAttachedNars(String classifier) throws MojoExecutionException,
            MojoFailureException {
        // FIXME should this be runtime ?
        // FIXME, hardcoded
        String[] types = { "jni", "shared" };

        for (int t = 0; t < types.length; t++) {
            List dependencies = getAttachedNarDependencies("compile",
                    classifier, types[t]);
            for (Iterator i = dependencies.iterator(); i.hasNext();) {
                Artifact dependency = (Artifact) i.next();
                System.err.println("Unpack " + dependency);
                File file = getNarFile(dependency);
                File narLocation = new File(file.getParentFile(), "nar");
                File flagFile = new File(narLocation, FileUtils.basename(file
                        .getPath(), "." + NAR_EXTENSION)
                        + ".flag");

                boolean process = false;
                if (!narLocation.exists()) {
                    narLocation.mkdirs();
                    process = true;
                } else if (!flagFile.exists()) {
                    process = true;
                } else if (file.lastModified() > flagFile.lastModified()) {
                    process = true;
                }

                if (process) {
                    try {
                        unpackNar(file, narLocation);
                        FileUtils.fileDelete(flagFile.getPath());
                        FileUtils.fileWrite(flagFile.getPath(), "");
                    } catch (IOException e) {
                        getLog().info(
                                "Cannot create flag file: "
                                        + flagFile.getPath());
                    }
                }
            }
        }
    }

    private void unpackNar(File file, File location)
            throws MojoExecutionException {
        try {
            UnArchiver unArchiver;
            unArchiver = getArchiverManager().getUnArchiver(NAR_ROLE_HINT);
            unArchiver.setSourceFile(file);
            unArchiver.setDestDirectory(location);
            unArchiver.extract();
        } catch (IOException e) {
            throw new MojoExecutionException("Error unpacking file: " + file
                    + " to: " + location, e);
        } catch (NoSuchArchiverException e) {
            throw new MojoExecutionException("Error unpacking file: " + file
                    + " to: " + location, e);
        } catch (ArchiverException e) {
            throw new MojoExecutionException("Error unpacking file: " + file
                    + " to: " + location, e);
        }
    }

}
