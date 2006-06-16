// Copyright FreeHEP, 2005-2006.
package org.freehep.maven.nar;


/**
 * Sets up a library to create
 *
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: src/main/java/org/freehep/maven/nar/Library.java 0a36823a3ca9 2006/06/16 17:45:25 duns $
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
    
    /**
     * Link with stdcpp if necessary
     * Defaults to true.
     * 
     * @parameter expression=""
     */
    protected boolean linkCPP = true;
    
    public String getType() {
        return type;
    }
    
    public boolean linkCPP() {
        return linkCPP;
    }
}

