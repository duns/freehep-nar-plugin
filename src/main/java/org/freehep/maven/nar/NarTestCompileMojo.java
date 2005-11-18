// Copyright FreeHEP, 2005.
package org.freehep.maven.nar;

import java.io.*;
import java.util.*;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.artifact.Artifact;

import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.PropertyUtils;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import net.sf.antcontrib.cpptasks.CCTask;
import net.sf.antcontrib.cpptasks.CompilerDef;
import net.sf.antcontrib.cpptasks.CompilerEnum;
import net.sf.antcontrib.cpptasks.CUtil;
import net.sf.antcontrib.cpptasks.OptimizationEnum;
import net.sf.antcontrib.cpptasks.OutputTypeEnum;
import net.sf.antcontrib.cpptasks.RuntimeType;

import net.sf.antcontrib.cpptasks.types.CompilerArgument;
import net.sf.antcontrib.cpptasks.types.ConditionalFileSet;
import net.sf.antcontrib.cpptasks.types.DefineArgument;
import net.sf.antcontrib.cpptasks.types.DefineSet;
import net.sf.antcontrib.cpptasks.types.IncludePath;
import net.sf.antcontrib.cpptasks.types.LibrarySet;
import net.sf.antcontrib.cpptasks.types.LibraryTypeEnum;

/**
 * @description Test Compile native source files.
 * @goal nar-testCompile
 * @phase test-compile
 * @requiresDependencyResolution test
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: src/main/java/org/freehep/maven/nar/NarTestCompileMojo.java eec048018869 2005/11/18 06:31:36 duns $
 */
public class NarTestCompileMojo extends AbstractCompileMojo {
              
    public void execute() throws MojoExecutionException, MojoFailureException { 
        // make sure destination is there
        getTargetDirectory().mkdirs();

        for (Iterator i=getTests().iterator(); i.hasNext(); ) {
            createTest(getAntProject(), (Test)i.next());  
        }
    }                
        
    private void createTest(Project antProject, Test test) throws MojoExecutionException, MojoFailureException {
        String type = "test";
        System.err.println("TESTING "+test.getName());
                            
        // configure task
        CCTask task = new CCTask();
        task.setProject(antProject);          

        // outtype
        OutputTypeEnum outTypeEnum = new OutputTypeEnum();
        outTypeEnum.setValue("executable");
        task.setOuttype(outTypeEnum);

        // outDir
        File outDir = new File(getTargetDirectory(), "bin");
        outDir = new File(outDir, getAOL());
        outDir = new File(outDir, test.getLink());
        outDir.mkdirs();

        // outFile
        File outFile = new File(outDir, test.getName());
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
        task.addConfiguredCompiler(getCpp().getCompiler(mavenProject, antProject, getOS(), getDefaults(), getAOLKey(), type, test.getName()));

        // add C compiler
        // FIXME use this as param
        task.addConfiguredCompiler(getC().getCompiler(mavenProject, antProject, getOS(), getDefaults(), getAOLKey(), type, test.getName()));

        // add Fortran compiler
        // FIXME use this as param
        task.addConfiguredCompiler(getFortran().getCompiler(mavenProject, antProject, getOS(), getDefaults(), getAOLKey(), type, test.getName()));

        // add java include paths 
        // FIXME, get rid of task
        getJava().addIncludePaths(mavenProject, task, this, type);
        
        // add dependency include paths
        for (Iterator i=getNarDependencies().iterator(); i.hasNext(); ) {
            File include = new File(getNarFile((Artifact)i.next()).getParentFile(), "nar/include");
            if (include.exists()) {
                task.createIncludePath().setPath(include.getPath());
            }
        }
                
        // add linker
        task.addConfiguredLinker(getLinker().getLinker(this, antProject, getOS(), getDefaults(), getAOLKey()+"linker.", type));

        // FIXME hardcoded values
        String libName = getFinalName();
        File includeDir = new File(mavenProject.getBuild().getDirectory(), "nar/include");
        File libDir = new File(mavenProject.getBuild().getDirectory(), "nar/lib/"+getAOL()+"/"+test.getLink());   
        
        // copy shared library
        if (test.getLink().equals("shared")) {
            try {
                // defaults are Unix
                String libPrefix = getDefaults().getProperty(getAOLKey()+"lib.prefix", "lib");
                String libExt = getDefaults().getProperty(getAOLKey()+"shared.extension", "so");
                File copyDir = new File(getTargetDirectory(), (getOS().equals("Windows") ? "bin" : "lib")+"/"+getAOL()+"/"+test.getLink());
                FileUtils.copyFileToDirectory(new File(libDir, libPrefix+libName+"."+libExt), copyDir);
                if (!getOS().equals("Windows")) {
                    libDir = copyDir;
                }
            } catch (IOException e) {
                throw new MojoExecutionException("NAR: Could not copy shared library", e);
            }
        }
        
        // FIXME what about copying the other shared libs?

        // add include of this package
        if (includeDir.exists()) {
            task.createIncludePath().setLocation(includeDir); 
        }
        
        // add library of this package
        if (libDir.exists()) {
            LibrarySet libSet = new LibrarySet();
            libSet.setProject(antProject);
            libSet.setLibs(new CUtil.StringArrayBuilder(libName));
            LibraryTypeEnum libType = new LibraryTypeEnum();
            libType.setValue(test.getLink());
            libSet.setType(libType);
            libSet.setDir(libDir);        
            task.addLibset(libSet);
        }
               
        // add dependency libraries
        for (Iterator i=getNarDependencies().iterator(); i.hasNext(); ) {
            Artifact dependency = (Artifact)i.next();
            File lib = new File(getNarFile(dependency).getParentFile(), "nar/lib/"+getAOL()+"/"+test.getLink());
            if (lib.exists()) {
                LibrarySet libset = new LibrarySet();
                libset.setProject(antProject);
                libset.setLibs(new CUtil.StringArrayBuilder(dependency.getArtifactId()+"-"+dependency.getVersion()));
                libset.setDir(lib);
                task.addLibset(libset);
            }
        }

        if (getOS().equals("MacOSX")) {
            // add Framework for MacOS X              
            getJava().addMacOSXRuntime(task);
        } else {
            // Add JVM to linker
            // FIXME, use "this".
            getJava().addRuntime(antProject, task, getDefaults(), getJavaHome(), getAOLKey()+"java.");
        }        

        // execute
        try {
            task.execute();
        } catch (BuildException e) {
            throw new MojoExecutionException("NAR: Test-Compile failed", e);
        }
   }
   
    protected List getDependencies() {
        return mavenProject.getTestArtifacts();
    }   
    
    protected File getTargetDirectory() {
        return new File(mavenProject.getBuild().getDirectory(), "test-nar");
    }    
}
