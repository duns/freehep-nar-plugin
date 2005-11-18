// Copyright FreeHEP, 2005.
package org.freehep.maven.nar;

import java.io.*;

import org.apache.maven.plugin.MojoFailureException;

import org.apache.tools.ant.Project;

import net.sf.antcontrib.cpptasks.CUtil;
import net.sf.antcontrib.cpptasks.types.LibraryTypeEnum;
import net.sf.antcontrib.cpptasks.types.LibrarySet;

/**
 * Sets up a library to create
 *
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: src/main/java/org/freehep/maven/nar/Library.java eec048018869 2005/11/18 06:31:36 duns $
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

