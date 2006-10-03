// Copyright FreeHEP, 2005.
package org.freehep.maven.nar;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Tests NAR files. Currently does nothing.
 * 
 * @goal nar-test
 * @phase test
 * @requiresProject
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: src/main/java/org/freehep/maven/nar/NarTestMojo.java ef838d8b7f19 2006/10/03 21:41:57 duns $
 */
public class NarTestMojo extends AbstractNarMojo {
            
    public void execute() throws MojoExecutionException, MojoFailureException { 
    	if (shouldSkip()) return;
    }    
}
