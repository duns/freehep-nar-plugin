// Copyright FreeHEP, 2005.
package org.freehep.maven.nar;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;


/**
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: src/main/java/org/freehep/maven/nar/AbstractNarMojo.java 3edb5ca24db1 2007/06/15 22:04:56 duns $
 */
public abstract class AbstractNarMojo extends AbstractMojo implements NarConstants {
    
    /**
     * Skip running of NAR plugins (any) altogether.
     * 
     * @parameter expression="${nar.skip}" default-value="false"
     */
    private boolean skip;
    
    /**
     * Level of logging messages, 0 is minimum.
     * 
     * @parameter expression="${nar.logLevel}" default-value="0"
     */
    private int logLevel;
    
    /**
     * The Architecture for the nar,
     * Some choices are: "x86", "i386", "amd64", "ppc", "sparc", ...
     * Defaults to a derived value from ${os.arch}
     *
     * @parameter expression="${os.arch}"
     * @required
     */
    private String architecture;

    /**
     * The Operating System for the nar.
     * Some choices are: "Windows", "Linux", "MacOSX", "SunOS", ...
     * Defaults to a derived value from ${os.name}
     * FIXME table missing
     *
     * @parameter expression=""
     */
    private String os;

    /**
     * Architecture-OS-Linker name.
     * Defaults to: arch-os-linker.
     *
     * @parameter expression=""
     */
    private String aol;

    /**
     * Linker
     *
     * @parameter expression=""
     */
    private Linker linker;
     
    /**
     * @parameter expression="${project.build.directory}"
     * @readonly
     */
    private File outputDirectory;

    /**
     * @parameter expression="${project.build.finalName}"
     * @readonly
     */
    private String finalName;

    /**
     * Target directory for Nar file construction
     * Defaults to "${project.build.directory}/nar" for "nar-compile" goal
     * Defaults to "${project.build.directory}/test-nar" for "nar-testCompile" goal
     *
     * @parameter expression=""
     */
    private File targetDirectory;
     
    /**
     * @parameter expression="${project}"
     * @readonly
     * @required
     */
    private MavenProject mavenProject;
 
    
    protected boolean shouldSkip() {
    	return skip;
    }
    
    protected int getLogLevel() {
    	return logLevel;
    }
    
    protected String getArchitecture() {
    	architecture = NarUtil.getArchitecture(architecture);
        return architecture;
    }
    
    protected String getOS() {
    	os = NarUtil.getOS(os);    	
        return os;
    }
    
    protected String getAOL() throws MojoFailureException {
    	aol = NarUtil.getAOL(architecture, os, linker, aol);    	
        return aol;
    }
    
    protected Linker getLinker() {
    	linker = NarUtil.getLinker(linker);
        return linker;
    }
    
    protected File getOutputDirectory() {
        return outputDirectory;
    }
   
    protected String getFinalName() {
        return finalName;
    }

    protected String getAOLKey() throws MojoFailureException {
    	return NarUtil.getAOLKey(architecture, os, linker);    	
    }
    
    protected File getTargetDirectory() {
        if (targetDirectory == null) {
            targetDirectory = new File(mavenProject.getBuild().getDirectory(), "nar");
        }
        return targetDirectory;
    }
              
    protected MavenProject getMavenProject() {
        return mavenProject;
    }
}
