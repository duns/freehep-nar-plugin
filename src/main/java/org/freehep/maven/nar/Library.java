// Copyright FreeHEP, 2005-2007.
package org.freehep.maven.nar;

/**
 * Sets up a library to create
 * 
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: src/main/java/org/freehep/maven/nar/Library.java 22f054423067 2007/06/15 23:34:05 duns $
 */
public class Library {

	public static final String STATIC = "static";
	public static final String SHARED = "shared";
	public static final String EXECUTABLE = "executable";
	public static final String JNI = "jni";
	public static final String PLUGIN = "plugin";

	/**
	 * Type of the library to generate. Possible choices are: "plugin",
	 * "shared", "static" or "jni". Defaults to "shared".
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

	public String getType() {
		return type;
	}

	public boolean linkCPP() {
		return linkCPP;
	}
}
