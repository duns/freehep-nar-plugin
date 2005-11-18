// Copyright FreeHEP, 2005.
package org.freehep.maven.nar;

import org.apache.maven.plugin.MojoFailureException;

import org.apache.tools.ant.Project;

import net.sf.antcontrib.cpptasks.CUtil;
import net.sf.antcontrib.cpptasks.types.LibraryTypeEnum;
import net.sf.antcontrib.cpptasks.types.SystemLibrarySet;

/**
 * Keeps info on a system library
 *
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: src/main/java/org/freehep/maven/nar/SysLib.java eec048018869 2005/11/18 06:31:36 duns $
 */
public class SysLib {

    /**
     * Name of the system library
     *
     * @parameter expression=""
     * @required
     */
    private String name;

    /**
     * Type of linking for this system library
     *
     * @parameter expression="" default-value="shared"
     * @required
     */
    private String type = "shared";

    public SystemLibrarySet getSysLibSet(Project antProject) throws MojoFailureException {
        if (name == null) {
            throw new MojoFailureException("NAR: Please specify <Name> as part of <SysLib>");
        }
        SystemLibrarySet sysLibSet = new SystemLibrarySet();
        sysLibSet.setProject(antProject);
        sysLibSet.setLibs(new CUtil.StringArrayBuilder(name));
        LibraryTypeEnum sysLibType = new LibraryTypeEnum();
        sysLibType.setValue(type);
        sysLibSet.setType(sysLibType);
        return sysLibSet;
    }    
}
