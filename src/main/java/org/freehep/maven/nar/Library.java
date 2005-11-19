// Copyright FreeHEP, 2005.
package org.freehep.maven.nar;


/**
 * Sets up a library to create
 *
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: src/main/java/org/freehep/maven/nar/Library.java bcdae088c368 2005/11/19 07:52:18 duns $
 */
public class Library {

    /**
     * Type of the library to generate.
     * Possible choices are: "plugin", "shared", "static" or "jni".
     * Defaults to "shared".
     * 
     * @parameter expression=""
     */
    protected String type = "shared";
    
    public String getType() {
        return type;
    }
}

