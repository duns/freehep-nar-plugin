// Copyright FreeHEP, 2005.
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
 * @version $Id: src/main/java/org/freehep/maven/nar/NarUnpackMojo.java bcdae088c368 2005/11/19 07:52:18 duns $
 */
public class NarUnpackMojo extends AbstractDependencyMojo {
            
    public void execute() throws MojoExecutionException, MojoFailureException {        
        
        List dependencies = getAllNarDependencies();
        for (Iterator i=dependencies.iterator(); i.hasNext(); ) {
            Artifact dependency = (Artifact)i.next();
            File file = getNarFile(dependency);
            File narLocation = file.getParentFile();
            File flagFile = new File(narLocation, "nar/"+FileUtils.basename(file.getPath(), "."+NAR_EXTENSION)+".flag");
            
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
                    FileUtils.fileWrite(flagFile.getPath(), "");                 
                } catch (IOException e) {
                    getLog().info("Cannot create flag file: "+flagFile.getPath());
                }
            }
        }
    }
    
    protected List getDependencies() {
        return mavenProject.getRuntimeArtifacts();
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
