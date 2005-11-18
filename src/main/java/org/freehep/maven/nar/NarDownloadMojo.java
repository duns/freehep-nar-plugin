// Copyright FreeHEP, 2005.
package org.freehep.maven.nar;

import java.io.*;
import java.util.*;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.maven.artifact.Artifact;

import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.util.FileUtils;

/**
 * @description Download NAR files.
 * @goal nar-download
 * @phase generate-sources
 * @requiresProject
 * @requiresDependencyResolution
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: src/main/java/org/freehep/maven/nar/NarDownloadMojo.java eec048018869 2005/11/18 06:31:36 duns $
 */
public class NarDownloadMojo extends AbstractDependencyMojo {
            
    public void execute() throws MojoExecutionException, MojoFailureException {        
        
        List dependencies = getNarDependencies();
        for (Iterator i=dependencies.iterator(); i.hasNext(); ) {
            Artifact dependency = (Artifact)i.next();
            System.err.println("Download "+dependency);
        }
    }
    
    public List getDependencies() {
        return mavenProject.getRuntimeArtifacts();
    }    
}
