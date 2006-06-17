// Copyright FreeHEP, 2005.
package org.freehep.maven.nar;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * @description Compiles class files into headers using "javah".
 * @goal nar-javah
 * @phase compile
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: src/main/java/org/freehep/maven/nar/NarJavahMojo.java aaed00b12053 2006/06/17 00:35:37 duns $
 */
public class NarJavahMojo extends AbstractNarMojo {
    
    public void execute() throws MojoExecutionException {
        getJavah().execute(getMavenProject(), getLog());
    }    
}