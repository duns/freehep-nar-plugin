// Copyright FreeHEP, 2005.
package org.freehep.maven.nar;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import net.sf.antcontrib.cpptasks.CUtil;
import net.sf.antcontrib.cpptasks.LinkerDef;
import net.sf.antcontrib.cpptasks.LinkerEnum;
import net.sf.antcontrib.cpptasks.types.LibrarySet;
import net.sf.antcontrib.cpptasks.types.LibraryTypeEnum;
import net.sf.antcontrib.cpptasks.types.LinkerArgument;
import net.sf.antcontrib.cpptasks.types.SystemLibrarySet;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.tools.ant.Project;

/**
 * Linker tag
 *
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: src/main/java/org/freehep/maven/nar/Linker.java bcdae088c368 2005/11/19 07:52:18 duns $
 */
public class Linker {

    /**
     * The Linker
     * Some choices are: "msvc", "g++", "CC", "icpc", ...
     * Default is Architecture-OS-Linker specific:
     * FIXME: table missing
     *
     * @parameter expression=""
     */
    private String name;  
    
    /**
     * Enables or disables incremental linking.
     *
     * @parameter expression="" default-value="false"
     * @required
     */
    private boolean incremental = false;

    /**
     * Enables or disables the production of a map file.
     *
     * @parameter expression="" default-value="false"
     * @required
     */
    private boolean map = false;

    /**
     * Options for the linker
     * Defaults to Architecture-OS-Linker specific values.
     * FIXME table missing
     *
     * @parameter expression=""
     */
    private List options;

    /**
     * (Windows only, Shared libs only) list of definition files.
     * Defaults to none
     *
     * @parameter expression=""
     */
    private List definitions;

    /**
     * Adds libraries to the linker.
     * 
     * @parameter expression=""
     */
    private List/*<Lib>*/ libs; 
     
    /**
     * Adds system libraries to the linker.
     * 
     * @parameter expression=""
     */
    private List/*<SysLib>*/ sysLibs; 

    public String getName(Properties defaults, String prefix) throws MojoFailureException {
        if (name == null) {
            name = defaults.getProperty(prefix+"linker");
        }
        if (name == null) {
            throw new MojoFailureException("NAR: Please specify a <Name> as part of <Linker>");
        }        
        return name;
    }

    public LinkerDef getLinker(AbstractDependencyMojo mojo, Project antProject, String os, Properties defaults, 
                               String prefix, String type) 
                throws MojoFailureException, MojoExecutionException {
        if (name == null) {
            throw new MojoFailureException("NAR: Please specify a <Name> as part of <Linker>");
        }        

        LinkerDef linker = new LinkerDef();
        linker.setProject(antProject);
        LinkerEnum linkerEnum = new LinkerEnum();
        linkerEnum.setValue(name);
        linker.setName(linkerEnum);

        // incremental, map
        linker.setIncremental(incremental);
        linker.setMap(map);

        // Add definitions (Window only)
        if (os.equals("Windows") && type.equals("shared") && (definitions != null)) {
            for (Iterator i=definitions.iterator(); i.hasNext(); ) {
                LinkerArgument arg = new LinkerArgument();
                arg.setValue("/def:"+(String)i.next());
                linker.addConfiguredLinkerArg(arg);
            }
        }

        // Add options to linker
        if (options != null) {
            for (Iterator i=options.iterator(); i.hasNext(); ) {
                LinkerArgument arg = new LinkerArgument();
                arg.setValue((String)i.next());
                linker.addConfiguredLinkerArg(arg);
            }
        } else {
            String options = defaults.getProperty(prefix+"options");
            if (options != null) {
                String[] option = options.split(" ");
                for (int i=0; i<option.length; i++) {
                    LinkerArgument arg = new LinkerArgument();
                    arg.setValue(option[i]);
                    linker.addConfiguredLinkerArg(arg);
                }          
            }
        }

        // Add Libraries to linker
        if (libs != null) {
            for (Iterator i=libs.iterator(); i.hasNext(); ) {
                Lib lib = (Lib)i.next();
                lib.addLibSet(mojo, linker, antProject);
            }
        } else {
            String libsList = defaults.getProperty(prefix+"libs");
            if (libsList != null) {
                String[] lib = libsList.split(", ");
                for (int i=0; i<lib.length; i++) {
                    String[] libInfo = lib[i].split(":", 3);
                    LibrarySet libSet = new LibrarySet();
                    libSet.setProject(antProject);
                    libSet.setLibs(new CUtil.StringArrayBuilder(libInfo[0]));
                    if (libInfo.length > 1) {
                        LibraryTypeEnum libType = new LibraryTypeEnum();
                        libType.setValue(libInfo[1]);
                        libSet.setType(libType);
                        if (libInfo.length > 2) {
                            libSet.setDir(new File(libInfo[2]));
                        }
                    }
                    
                    linker.addLibset(libSet);
                }            
            }
        }

        // Add System Libraries to linker
        if (sysLibs != null) {
            for (Iterator i=sysLibs.iterator(); i.hasNext(); ) {
                SysLib sysLib = (SysLib)i.next();
                linker.addSyslibset(sysLib.getSysLibSet(antProject));
            }
        } else {
            String sysLibsList = defaults.getProperty(prefix+"sysLibs");
            if (sysLibsList != null) {
                String[] sysLib = sysLibsList.split(", ");
                for (int i=0; i<sysLib.length; i++) {
                    String[] sysLibInfo = sysLib[i].split(":", 2);
                    SystemLibrarySet sysLibSet = new SystemLibrarySet();
                    sysLibSet.setProject(antProject);
                    sysLibSet.setLibs(new CUtil.StringArrayBuilder(sysLibInfo[0]));
                    if (sysLibInfo.length > 1) {
                        LibraryTypeEnum sysLibType = new LibraryTypeEnum();
                        sysLibType.setValue(sysLibInfo[1]);
                        sysLibSet.setType(sysLibType);
                    }
                    linker.addSyslibset(sysLibSet);
                }            
            }
        }

        return linker;
    }
}
