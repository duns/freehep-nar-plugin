// Copyright FreeHEP, 2005-2007.
package org.freehep.maven.nar;

import java.util.ArrayList;
import java.util.List;

/**
 * Sets up a library to create
 * 
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: plugin/src/main/java/org/freehep/maven/nar/Library.java c867ab546be1 2007/07/05 21:26:30 duns $
 */
public class Library implements Executable {

	public static final String STATIC = "static";
	public static final String SHARED = "shared";
	public static final String EXECUTABLE = "executable";
	public static final String JNI = "jni";
	public static final String PLUGIN = "plugin";

	/**
	 * Type of the library to generate. Possible choices are: "plugin",
	 * "shared", "static", "jni" or "executable". Defaults to "shared".
	 * 
	 * @parameter expression=""
	 */
	protected String type = "shared";

	/**
	 * Link with stdcpp if necessary Defaults to true.
	 * 
	 * @parameter expression=""
	 */
	protected boolean linkCPP = true;

	/**
	 * If specified will create the NarSystem class with methods
	 * to load a JNI library.
	 * 
	 * @parameter expression=""
	 */
	protected String packageName = null;
	
	/**
     * When true and if type is "executable" run this executable.
     * Defaults to false;
   	 * 
	 * @parameter expression=""
	 */
	protected boolean run=false;
	
	/**
	 * Arguments to be used for running this executable.
	 * Defaults to empty list. This option is 
	 * only used if run=true and type=executable.
	 * 
	 * @parameter expression=""
	 */
    protected List/*<String>*/ args = new ArrayList();

	public String getType() {
		return type;
	}

	public boolean linkCPP() {
		return linkCPP;
	}
	
	public String getPackageName() {
		return packageName;
	}
	
	public boolean shouldRun() {
		return run;
	}
	
    public List/*<String>*/ getArgs() {
    	return args;
    }	
}
