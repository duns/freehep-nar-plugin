// Copyright FreeHEP, 2005.
package org.freehep.maven.nar;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * @description Compiles class files into headers using "javah".
 * @goal nar-javah
 * @phase compile
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: src/main/java/org/freehep/maven/nar/NarJavahMojo.java 63e59ef830f9 2006/09/28 23:19:52 duns $
 */
public class NarJavahMojo extends AbstractNarMojo {
    
    public void execute() throws MojoExecutionException {
    	if (shouldSkip()) return;
    	
        getJavah().execute(getMavenProject(), getLog());
    }    
}