// Copyright FreeHEP, 2005.
package org.freehep.maven.nar;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.util.PropertyUtils;


/**
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: src/main/java/org/freehep/maven/nar/AbstractNarMojo.java b622bcaea4f3 2006/06/16 17:52:04 duns $
 */
public abstract class AbstractNarMojo extends AbstractMojo {

    protected final String NAR_EXTENSION = "nar";
    protected final String NAR_NO_ARCH = "noarch";
    protected final String NAR_ROLE_HINT = "nar-library";
    protected final String NAR_TYPE = NAR_ROLE_HINT;
//    protected final String NAR_PROPERTIES = "nar.properties";
       
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
    protected MavenProject mavenProject;
 
    /**
     * @parameter expression="${localRepository}"
     * @required
     * @readonly
     */
    protected ArtifactRepository localRepository;

    /**
     * Maven ArtifactFactory.
     *
     * @parameter expression="${component.org.apache.maven.artifact.factory.ArtifactFactory}"
     * @required
     * @readonly
     */
    protected ArtifactFactory artifactFactory;

    /**
     * To look up Archiver/UnArchiver implementations
     *
     * @parameter expression="${component.org.codehaus.plexus.archiver.manager.ArchiverManager}"
     * @required
     */
    protected ArchiverManager archiverManager;


    private Properties defaults;
    private String aolKey;
    
    public Properties getDefaults() throws MojoFailureException {
        // read properties file with defaults
        if (defaults == null) {
            defaults = PropertyUtils.loadProperties(AbstractNarMojo.class.getResourceAsStream("aol.properties"));
        }
        if (defaults == null) throw new MojoFailureException("NAR: Could not load default properties file: 'aol.properties'.");

        return defaults;
    }
    
    protected String getArchitecture() {
        return architecture;
    }
    
    protected String getOS() {
        // adjust OS if not given
        if (os == null) {
            os = System.getProperty("os.name");
            if (os.startsWith("Windows")) os = "Windows";
            if (os.equals("Mac OS X")) os = "MacOSX";
        }
        return os;
    }
    
    protected File getJavaHome() {
        // adjust JavaHome
        if (javaHome == null) {
            javaHome = new File(System.getProperty("java.home"));
            if (!getOS().equals("MacOSX")) {
                javaHome = new File(javaHome, "..");
            }
            getLog().info("JavaHome '"+javaHome+"'");
        }
        return javaHome;       
    }
    
    protected String getAOL() throws MojoFailureException {
        // adjust aol
        if (aol == null) {
            aol = getArchitecture()+"-"+getOS()+"-"+getLinkerName();       
            getLog().info("NAR target '"+aol+"'");
        }
        return aol;
    }
    
    protected Linker getLinker() {
        if (linker == null) {
            linker = new Linker();
        }
        return linker;
    }
    
    protected File getOutputDirectory() {
        return outputDirectory;
    }
   
    protected String getFinalName() {
        return finalName;
    }

    protected String getAOLKey() throws MojoFailureException {
        if (aolKey == null) {
            // construct AOL key prefix
            aolKey = getArchitecture()+"."+getOS()+"."+getLinkerName()+".";    
        }
        return aolKey;
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
   
    private String getLinkerName() throws MojoFailureException {
        return getLinker().getName(getDefaults(), getArchitecture()+"."+getOS()+".");        
    }
}
