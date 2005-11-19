// Copyright FreeHEP, 2005.
package org.freehep.maven.nar;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * @description Compiles class files into headers using "javah".
 * @goal nar-javah
 * @phase compile
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: src/main/java/org/freehep/maven/nar/NarJavahMojo.java bcdae088c368 2005/11/19 07:52:18 duns $
 */
public class NarJavahMojo extends AbstractNarMojo {
    
    public void execute() throws MojoExecutionException {
        getJavah().execute(mavenProject, getLog());
    }    
}