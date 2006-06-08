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
 * @version $Id: src/main/java/org/freehep/maven/nar/NarUnpackMojo.java fb2f54cb3103 2006/06/08 23:31:35 duns $
 */
public class NarUnpackMojo extends AbstractDependencyMojo {
            
    public void execute() throws MojoExecutionException, MojoFailureException {        
        
        List dependencies = getAllNarDependencies("compile");
        for (Iterator i=dependencies.iterator(); i.hasNext(); ) {
            Artifact dependency = (Artifact)i.next();
            File file = getNarFile(dependency);
            File narLocation = new File(file.getParentFile(), "nar");
            File flagFile = new File(narLocation, FileUtils.basename(file.getPath(), "."+NAR_EXTENSION)+".flag");
            
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
                    getLog().info("Cannot create flag file: "+flagFile.getPath());
                }
            }
        }
    }
        
    private void unpackNar(File file, File location) throws MojoExecutionException {
        try {
            UnArchiver unArchiver;
            unArchiver = archiverManager.getUnArchiver(NAR_ROLE_HINT);
            unArchiver.setSourceFile(file);
            unArchiver.setDestDirectory(location);
            unArchiver.extract();
        } catch (IOException e) {
            throw new MojoExecutionException("Error unpacking file: "+file+" to: "+location, e);
        } catch (NoSuchArchiverException e) {
            throw new MojoExecutionException("Error unpacking file: "+file+" to: "+location, e);
        } catch (ArchiverException e) {
            throw new MojoExecutionException("Error unpacking file: "+file+" to: "+location, e);
        } 
    }
    
}
