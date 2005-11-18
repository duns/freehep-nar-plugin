// Copyright FreeHEP, 2005.
package org.freehep.maven.nar;

import java.io.*;
import java.util.*;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import org.apache.tools.ant.Project;

import net.sf.antcontrib.cpptasks.CCTask;
import net.sf.antcontrib.cpptasks.CUtil;
import net.sf.antcontrib.cpptasks.types.CommandLineArgument;
import net.sf.antcontrib.cpptasks.types.LibrarySet;
import net.sf.antcontrib.cpptasks.types.LibraryTypeEnum;
import net.sf.antcontrib.cpptasks.types.LinkerArgument;
import net.sf.antcontrib.cpptasks.types.SystemLibrarySet;

/**
 * Java specifications for NAR
 *
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: src/main/java/org/freehep/maven/nar/Java.java eec048018869 2005/11/18 06:31:36 duns $
 */
public class Java {

    /**
     * Add Java includes to includepath
     *
     * @parameter expression="" default-value="false"
     * @required
     */
    private boolean include = false;

    /**
     * Java Include Paths, relative to a derived ${java.home}.
     * Defaults to: "${java.home}/include" and "${java.home}/include/<i>os-specific</i>".
     *
     * @parameter expression=""
     */
    private List includePaths;

    /**
     * Add Java Runtime to linker
     *
     * @parameter expression="" default-value="false"
     * @required
     */
    private boolean link = false;

    /**
     * Relative path from derived ${java.home} to the java runtime to link with
     * Defaults to Architecture-OS-Linker specific value.
     * FIXME table missing
     *
     * @parameter expression=""
     */
    private String runtimeDirectory;
     
    /**
     * Name of the runtime
     *
     * @parameter expression="" default-value="jvm"
     */
    private String runtime = "jvm";
    
    // FIXME, NarCompileMojo, change to AbstractCompileMojo 
    public void addIncludePaths(MavenProject mavenProject, CCTask task, AbstractCompileMojo mojo, String outType) throws MojoFailureException {
        if (include || mojo.getJavah().getJniDirectory(mavenProject).exists()) {
            if (includePaths != null) {
                for (Iterator i=includePaths.iterator(); i.hasNext(); ) {
                    String path = (String)i.next();
                    task.createIncludePath().setPath(new File(mojo.getJavaHome(), path).getPath());
                }
            } else {
                String prefix = mojo.getAOLKey()+"java.";
                String[] path = mojo.getDefaults().getProperty(prefix+"include").split(";");
                if (path != null) {
                    for (int i=0; i<path.length; i++) {
                        task.createIncludePath().setPath(new File(mojo.getJavaHome(), path[i]).getPath());
                    }
                } else {
                    if (outType.equals("jni")) throw new MojoFailureException("NAR: Please specify <IncludePaths> as part of <Java>");
                }
            }
        }
    }
    
    public void addRuntime(Project antProject, CCTask task, Properties defaults, File javaHome, String prefix) throws MojoFailureException {
        if (link) {
            if (runtimeDirectory == null) {
                runtimeDirectory = defaults.getProperty(prefix+"runtimeDirectory");
                if (runtimeDirectory == null) {
                    throw new MojoFailureException("NAR: Please specify a <RuntimeDirectory> as part of <Java>");
                }
            }
            LibrarySet libset = new LibrarySet();
            libset.setProject(antProject);
            libset.setLibs(new CUtil.StringArrayBuilder(runtime));
            libset.setDir(new File(javaHome, runtimeDirectory));
            task.addLibset(libset);
        }
    }
    
    public void addMacOSXRuntime(CCTask task) {
        if (link) {
            CommandLineArgument.LocationEnum end = new CommandLineArgument.LocationEnum();
            end.setValue("end");
            
            // add as argument rather than library to avoid argument quoting
            LinkerArgument framework = new LinkerArgument();
            framework.setValue("-framework");
            framework.setLocation(end);
            task.addConfiguredLinkerArg(framework);

            LinkerArgument javavm = new LinkerArgument();
            javavm.setValue("JavaVM");
            javavm.setLocation(end);
            task.addConfiguredLinkerArg(javavm);
        }
    }
}
