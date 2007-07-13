// Copyright FreeHEP, 2005-2007.
package org.freehep.maven.nar;

/**
 * Cpp compiler tag
 *
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: plugin/src/main/java/org/freehep/maven/nar/Cpp.java f934ad2b8948 2007/07/13 14:17:10 duns $
 */
public class Cpp extends Compiler {
	Cpp(AbstractCompileMojo mojo) {
		super(mojo);
	}
	
    public String getName() {
        return "cpp";
    }   
}
