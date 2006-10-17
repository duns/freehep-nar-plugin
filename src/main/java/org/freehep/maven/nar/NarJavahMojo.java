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
 * @version $Id: src/main/java/org/freehep/maven/nar/NarJavahMojo.java 2bfc7ab24863 2006/10/17 00:24:06 duns $
 */
public class NarJavahMojo extends AbstractCompileMojo {
    
    public void execute() throws MojoExecutionException {
    	if (shouldSkip()) return;
    	
        getJavah().execute(getMavenProject(), getLog());
    }    
}