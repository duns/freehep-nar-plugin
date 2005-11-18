// Copyright FreeHEP, 2005.
package org.freehep.maven.nar;

import java.io.*;
import java.util.*;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.util.FileUtils;

/**
 * @description Test NAR files.
 * @goal nar-test
 * @phase test
 * @requiresProject
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: src/main/java/org/freehep/maven/nar/NarTestMojo.java eec048018869 2005/11/18 06:31:36 duns $
 */
public class NarTestMojo extends AbstractNarMojo {
            
    public void execute() throws MojoExecutionException, MojoFailureException {        
    }    
}
