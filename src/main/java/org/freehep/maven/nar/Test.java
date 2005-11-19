// Copyright FreeHEP, 2005.
package org.freehep.maven.nar;

import org.apache.maven.plugin.MojoFailureException;

/**
 * Sets up a test to create
 *
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: src/main/java/org/freehep/maven/nar/Test.java bcdae088c368 2005/11/19 07:52:18 duns $
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

