// Copyright FreeHEP, 2005-2007.
package org.freehep.maven.nar;

/**
 * C compiler tag
 *
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: plugin/src/main/java/org/freehep/maven/nar/C.java f934ad2b8948 2007/07/13 14:17:10 duns $
 */
public class C extends Compiler { 
    
	C(AbstractCompileMojo mojo) {
		super(mojo);
	}
	
    public String getName() {
        return "c";
    }   
}
