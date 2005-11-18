// Copyright FreeHEP, 2005.
package org.freehep.maven.nar;

import java.io.*;

import org.apache.maven.plugin.MojoFailureException;

import org.apache.tools.ant.Project;

import net.sf.antcontrib.cpptasks.CUtil;
import net.sf.antcontrib.cpptasks.types.LibraryTypeEnum;
import net.sf.antcontrib.cpptasks.types.LibrarySet;

/**
 * Sets up a test to create
 *
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: src/main/java/org/freehep/maven/nar/Test.java eec048018869 2005/11/18 06:31:36 duns $
 */
public class Test {

    /**
     * Name of the test to create
     *
     * @required
     * @parameter expression=""
     */
    protected String name = null;

    /**
     * Type of linking used for this test
     * Possible choices are: "shared" or "static".
     * Defaults to "shared".
     * 
     * @parameter expression=""
     */
    protected String link = "shared";
    
    public String getName() throws MojoFailureException {
        if (name == null) throw new MojoFailureException("NAR: Please specify <Name> as part of <Test>");
        return name;
    }
    
    public String getLink() {
        return link;
    }
}

