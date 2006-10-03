// Copyright FreeHEP, 2005.
package org.freehep.maven.nar;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Compiles class files into c/c++ headers using "javah". 
 * Any class file that contains methods that were declared
 * "native" will be run through javah.
 *
 * @goal nar-javah
 * @phase compile
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: src/main/java/org/freehep/maven/nar/NarJavahMojo.java ef838d8b7f19 2006/10/03 21:41:57 duns $
 */
public class NarJavahMojo extends AbstractNarMojo {
    
    public void execute() throws MojoExecutionException {
    	if (shouldSkip()) return;
    	
        getJavah().execute(getMavenProject(), getLog());
    }    
}