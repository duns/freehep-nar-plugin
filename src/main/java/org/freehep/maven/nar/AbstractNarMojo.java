// Copyright FreeHEP, 2005.
package org.freehep.maven.nar;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.manager.ArchiverManager;


/**
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: src/main/java/org/freehep/maven/nar/AbstractNarMojo.java 8c1595ae1e05 2006/10/13 23:26:37 duns $
 */
public abstract class AbstractNarMojo extends AbstractMojo implements NarConstants {
    
    /**
     * Skip running of NAR plugins (any) altogether
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
     * The home of the Java system.
     * Defaults to a derived value from ${java.home} which is OS specific.
     *
     * @parameter expression=""
     * @readonly
     */
    private File javaHome;
     
    /**
     * Architector-OS-Linker name.
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
     * Defaults to "${project.build.directory}/nar" for "compile" goal
     * Defaults to "${project.build.directory}/test-nar" for "compile-test" goal
     *
     * @parameter expression=""
     */
    private File targetDirectory;
 
    /**
     * Name of the output
     *
     * @parameter expression="${project.artifactId}-${project.version}"
     */
    private String output;
    
    /**
     * Fail on compilation/linking error.
     *
     * @parameter expression="" default-value="true"
     * @required
     */
    private boolean failOnError;

    /**
     * Sets the type of runtime library, possible values "dynamic", "static".
     *
     * @parameter expression="" default-value="dynamic"
     * @required
     */
    private String runtime;

    /**
     * Set use of libtool. If set to true, the "libtool " will be prepended to the command line for compatible processors.
     *
     * @parameter expression="" default-value="false"
     * @required
     */
    private boolean libtool;

    /**
     * Javah info
     *
     * @parameter expression=""
     */
    private Javah javah;
    
    /**
     * C++ Compiler
     *
     * @parameter expression=""
     */
    private Cpp cpp;

    /**
     * C Compiler
     *
     * @parameter expression=""
     */
    private C c;

    /**
     * Fortran Compiler
     *
     * @parameter expression=""
     */
    private Fortran fortran;

    /**
     * Java info for includes and linking
     *
     * @parameter expression=""
     */
    private Java java;
    
    /**
     * List of libraries to create
     *
     * @parameter expression=""
     */
    private List libraries;

    /**
     * List of tests to create
     *
     * @parameter expression=""
     */
    private List tests;

    /**
     * @parameter expression="${project}"
     * @readonly
     * @required
     */
    private MavenProject mavenProject;
 
    /**
     * @parameter expression="${localRepository}"
     * @required
     * @readonly
     */
    private ArtifactRepository localRepository;

    /**
     * Maven ArtifactFactory.
     *
     * @parameter expression="${component.org.apache.maven.artifact.factory.ArtifactFactory}"
     * @required
     * @readonly
     */
    private ArtifactFactory artifactFactory;

    /**
     * To look up Archiver/UnArchiver implementations
     *
     * @parameter expression="${component.org.codehaus.plexus.archiver.manager.ArchiverManager}"
     * @required
     */
    private ArchiverManager archiverManager;
    
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

    protected File getJavaHome() {
    	javaHome = NarUtil.getJavaHome(javaHome, os);    	
    	return javaHome;       
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
        
    protected List getLibraries() {
        if (libraries == null) libraries = Collections.EMPTY_LIST;
        return libraries;
    }
   
    protected List getTests() {
        if (tests == null) tests = Collections.EMPTY_LIST;
        return tests;
    }
   
    protected Javah getJavah() {
        if (javah == null) javah = new Javah();
        return javah;
    }
   
    protected C getC() {
        if (c == null) c = new C();
        return c;
    }
   
    protected Cpp getCpp() {
        if (cpp == null) cpp = new Cpp();
        return cpp;
    }
   
    protected Fortran getFortran() {
        if (fortran == null) fortran = new Fortran();
        return fortran;
    }
    
    protected boolean useLibtool() {
        return libtool;
    }
    
    protected boolean failOnError() {
        return failOnError;
    }
    
    protected String getRuntime() {
        return runtime;
    }
    
    protected String getOutput() {
        return output;
    }
   
    protected Java getJava() {
        if (java == null) java = new Java();
        return java;
    }
   
    protected ArchiverManager getArchiverManager() {
        return archiverManager;
    }

    protected ArtifactFactory getArtifactFactory() {
        return artifactFactory;
    }

    protected ArtifactRepository getLocalRepository() {
        return localRepository;
    }

    protected MavenProject getMavenProject() {
        return mavenProject;
    }
}
