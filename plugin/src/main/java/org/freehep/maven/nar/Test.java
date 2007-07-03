// Copyright FreeHEP, 2005-2007.
package org.freehep.maven.nar;

import org.apache.maven.plugin.MojoFailureException;

/**
 * Sets up a test to create
 *
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: plugin/src/main/java/org/freehep/maven/nar/Test.java 1c0efd4f1b40 2007/07/03 21:40:25 duns $
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
    protected String link = Library.SHARED;

    /**
     * When true run this test.
     * Defaults to true;
     * 
     * @parameter expresssion=""
     */
	protected boolean run=true;
    
    public String getName() throws MojoFailureException {
        if (name == null) throw new MojoFailureException("NAR: Please specify <Name> as part of <Test>");
        return name;
    }
    
    public String getLink() {
        return link;
    }
    
    public boolean shouldRun() {
    	return run;
    }
}

