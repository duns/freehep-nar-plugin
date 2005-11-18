// Copyright FreeHEP, 2005.
package org.freehep.maven.nar;

import java.io.*;
import java.util.*;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import org.apache.tools.ant.Project;

import net.sf.antcontrib.cpptasks.CUtil;
import net.sf.antcontrib.cpptasks.LinkerDef;
import net.sf.antcontrib.cpptasks.types.LibraryTypeEnum;
import net.sf.antcontrib.cpptasks.types.LibrarySet;

/**
 * Keeps info on a library
 *
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: src/main/java/org/freehep/maven/nar/Lib.java eec048018869 2005/11/18 06:31:36 duns $
 */
public class Lib {

    /**
     * Name of the library, or a dependency groupId:artifactId if this library contains sublibraries
     *
     * @parameter expression=""
     * @required
     */
    private String name;

    /**
     * Type of linking for this library
     *
     * @parameter expression="" default-value="shared"
     * @required
     */
    private String type = "shared";

    /**
     * Location for this library
     *
     * @parameter expression=""
     * @required
     */
    private File directory;
    
    /**
     * Sub libraries for this library
     *
     * @parameter expression=""
     */
    private List/*<Lib>*/ libs; 

    public void addLibSet(AbstractDependencyMojo mojo, LinkerDef linker, Project antProject) throws MojoFailureException, MojoExecutionException {
        addLibSet(mojo, linker, antProject, name, directory);
    }

    private void addLibSet(AbstractDependencyMojo mojo, LinkerDef linker, Project antProject, String name, File dir) throws MojoFailureException, MojoExecutionException {
        if (name == null) {
            throw new MojoFailureException("NAR: Please specify <Name> as part of <Lib>");
        }
        if (libs == null) {
            if (dir == null) {
                throw new MojoFailureException("NAR: Please specify <Directory> as part of <Lib>");
            }
            LibrarySet libSet = new LibrarySet();
            libSet.setProject(antProject);
            libSet.setLibs(new CUtil.StringArrayBuilder(name));
            LibraryTypeEnum libType = new LibraryTypeEnum();
            libType.setValue(type);
            libSet.setType(libType);
            libSet.setDir(dir);
            linker.addLibset(libSet);        
        } else {
            List dependencies = mojo.getNarDependencies();
            for (Iterator i=libs.iterator(); i.hasNext(); ) {
                Lib lib = (Lib)i.next();
                String[] ids = name.split(":",2);
                if (ids.length != 2) {
                    throw new MojoFailureException("NAR: Please specify <Name> as part of <Lib> in format 'groupId:artifactId'");             
                }
                for (Iterator j=dependencies.iterator(); j.hasNext(); ) {
                    Artifact dependency = (Artifact)j.next();
                    if (dependency.getGroupId().equals(ids[0]) && dependency.getArtifactId().equals(ids[1])) {
                        File narDir = new File(mojo.getNarFile(dependency).getParentFile(), "nar/lib/"+mojo.getAOL()+"/"+lib.type);
                        String narName = dependency.getArtifactId()+"-"+lib.name+"-"+dependency.getVersion();
                        lib.addLibSet(mojo, linker, antProject, narName, narDir);
                    }
                }
            }
        }
    }    
}

