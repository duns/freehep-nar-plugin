// Copyright FreeHEP, 2005-2006.
package org.freehep.maven.nar;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import net.sf.antcontrib.cpptasks.CCTask;
import net.sf.antcontrib.cpptasks.CUtil;
import net.sf.antcontrib.cpptasks.OutputTypeEnum;
import net.sf.antcontrib.cpptasks.RuntimeType;
import net.sf.antcontrib.cpptasks.types.LibrarySet;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.codehaus.plexus.util.FileUtils;

/**
 * @description Compile native source files.
 * @goal nar-compile
 * @phase compile
 * @requiresDependencyResolution compile
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: src/main/java/org/freehep/maven/nar/NarCompileMojo.java a6d83bea2713 2006/06/12 20:51:42 duns $
 */
public class NarCompileMojo extends AbstractCompileMojo {
        
    public void execute() throws MojoExecutionException, MojoFailureException {                        
        // make sure destination is there
        getTargetDirectory().mkdirs();
                                                            
        for (Iterator i=getLibraries().iterator(); i.hasNext(); ) {
            createLibrary(getAntProject(), (Library)i.next());  
        }
               
        try {
            // FIXME, should the include paths be defined at a higher level ?
            getCpp().copyIncludeFiles(mavenProject, new File(getTargetDirectory(), "include"));
        } catch (IOException e) {
            throw new MojoExecutionException("NAR: could not copy include files", e);
        }
   }

    private void createLibrary(Project antProject, Library library) throws MojoExecutionException, MojoFailureException { 
        // configure task
        CCTask task = new CCTask();
        task.setProject(antProject);  
           
        // outtype
        OutputTypeEnum outTypeEnum = new OutputTypeEnum();
        String type = library.getType();
//        if (type.equals("jni")) {
//            outTypeEnum.setValue("plugin");
//        } else {
            outTypeEnum.setValue(type);
//        }
        task.setOuttype(outTypeEnum);

        // outDir
        File outDir = new File(getTargetDirectory(), "lib");
        outDir = new File(outDir, getAOL());
        outDir = new File(outDir, type);
        outDir.mkdirs();

        // outFile
        File outFile = new File(outDir, getOutput());
        getLog().info("NAR - output: '"+outFile+"'");
        task.setOutfile(outFile); 

        // object directory
        File objDir = new File(getTargetDirectory(), "obj");
        objDir = new File(objDir, getAOL());
        objDir.mkdirs();
        task.setObjdir(objDir);

        // failOnError, libtool
        task.setFailonerror(failOnError());
        task.setLibtool(useLibtool());

        // runtime
        RuntimeType runtimeType = new RuntimeType();
        runtimeType.setValue(getRuntime());
        task.setRuntime(runtimeType);
        
        // add C++ compiler
        // FIXME use this as param
        task.addConfiguredCompiler(getCpp().getCompiler(mavenProject, antProject, getOS(), getDefaults(), getAOLKey(), type, getOutput()));

        // add C compiler
        // FIXME use this as param
        task.addConfiguredCompiler(getC().getCompiler(mavenProject, antProject, getOS(), getDefaults(), getAOLKey(), type, getOutput()));

        // add Fortran compiler
        // FIXME use this as param
        task.addConfiguredCompiler(getFortran().getCompiler(mavenProject, antProject, getOS(), getDefaults(), getAOLKey(), type, getOutput()));

        // add javah include path
        File jniDirectory = getJavah().getJniDirectory(mavenProject);
        if (jniDirectory.exists()) task.createIncludePath().setPath(jniDirectory.getPath());    

        // add java include paths 
        // FIXME, get rid of task
        getJava().addIncludePaths(mavenProject, task, this, type);
        
        // add dependency include paths
        for (Iterator i=getNarDependencies("compile").iterator(); i.hasNext(); ) {
            // FIXME, handle multiple includes from one NAR
            File include = new File(getNarFile((Artifact)i.next()).getParentFile(), "nar/include");
            System.err.println("*** Include "+include);
            if (include.exists()) {
                task.createIncludePath().setPath(include.getPath());
            }
        }
                
        // add linker
        task.addConfiguredLinker(getLinker().getLinker(this, antProject, getOS(), getDefaults(), getAOLKey()+"linker.", type));

        // add dependency libraries
        if (type.equals("shared") || type.equals("jni")) {
            for (Iterator i=getNarDependencies("compile").iterator(); i.hasNext(); ) {
                Artifact dependency = (Artifact)i.next();
                // FIXME, what about shared linking
                // FIXME, getAOL() should be corrected.
                File lib = new File(getNarFile(dependency).getParentFile(), "nar/lib/"+"i386-Linux-gcc"+"/static");
                System.err.println("*** Lib "+lib);
                if (lib.exists()) {
                    LibrarySet libset = new LibrarySet();
                    libset.setProject(antProject);
                    // FIXME, pick up correct lib
                    libset.setLibs(new CUtil.StringArrayBuilder("packlib, dl, g2c"));
                            //dependency.getArtifactId()+"-"+dependency.getVersion()));
                    libset.setDir(lib);
                    System.err.println("*** LIBSET: "+libset);
                    task.addLibset(libset);
                }
            }
        }
                                  
        // Add JVM to linker
        if (!getOS().equals("MacOSX")) {
            // FIXME, use "this".
            getJava().addRuntime(antProject, task, getDefaults(), getJavaHome(), getAOLKey()+"java.");
        }        
        
        // execute
        try {
            task.execute();
        } catch (BuildException e) {
            throw new MojoExecutionException("NAR: Compile failed", e);
        }

        // rename output file on MacOSX
//        if (library.getType().equals("jni") && getOS().equals("MacOSX")) {
//            try {
//               FileUtils.rename(new File(outFile.getParent(), "lib"+outFile.getName()+".so"), new File(outFile.getParent(), "lib"+outFile.getName()+".jnilib"));
//            } catch (IOException e) {
//                throw new MojoExecutionException("NAR: could not rename output file", e);
//            }
//        }          
    }
}
