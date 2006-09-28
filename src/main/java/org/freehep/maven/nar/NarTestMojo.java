// Copyright FreeHEP, 2005.
package org.freehep.maven.nar;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @description Test NAR files.
 * @goal nar-test
 * @phase test
 * @requiresProject
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: src/main/java/org/freehep/maven/nar/NarTestMojo.java 63e59ef830f9 2006/09/28 23:19:52 duns $
 */
public class NarTestMojo extends AbstractNarMojo {
            
    public void execute() throws MojoExecutionException, MojoFailureException { 
    	if (shouldSkip()) return;
    }    
}
