// Copyright FreeHEP, 2005-2007.
package org.freehep.maven.nar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.compiler.util.scan.InclusionScanException;
import org.codehaus.plexus.compiler.util.scan.SourceInclusionScanner;
import org.codehaus.plexus.compiler.util.scan.StaleSourceScanner;
import org.codehaus.plexus.compiler.util.scan.mapping.SingleTargetSourceMapping;
import org.codehaus.plexus.compiler.util.scan.mapping.SuffixMapping;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

/**
 * Sets up the javah configuration
 *
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: plugin/src/main/java/org/freehep/maven/nar/Javah.java 353465868c1e 2007/07/20 14:59:21 duns $
 */
public class Javah {

    /**
     * Javah command to run.
     *
     * @parameter default-value="javah"
     */
    private String name = "javah";

    /**
     * Add boot class paths. By default none.
     *
     * @parameter
     */
    private List/*<File>*/ bootClassPaths = new ArrayList();
           
    /**
     * Add class paths. By default the classDirectory directory is included and all dependent classes.
     *
     * @parameter
     */
    private List/*<File>*/ classPaths = new ArrayList();
           
    /**
     * The target directory into which to generate the output.
     *
     * @parameter expression="${project.build.directory}/nar/javah-include"
     * @required
     */
    private File jniDirectory;
    
    /**
     * The class directory to scan for class files with native interfaces.
     *
     * @parameter expression="${project.build.directory}/classes"
     * @required
     */
    private File classDirectory;

    /**
     * The set of files/patterns to include
     * Defaults to "**\/*.class"
     *
     * @parameter
     */
    private Set includes = new HashSet();

    /**
     * A list of exclusion filters.
     *
     * @parameter
     */
    private Set excludes = new HashSet();

    /**
     * The granularity in milliseconds of the last modification
     * date for testing whether a source needs recompilation
     *
     * @parameter default-value="0"
     * @required
     */
    private int staleMillis = 0;

    /**
     * The directory to store the timestampfile for the processed aid files. Defaults to jniDirectory.
     * 
     * @parameter
     */
    private File timestampDirectory;

    /**
     * The timestampfile for the processed class files. Defaults to name of javah.
     * 
     * @parameter
     */
    private File timestampFile;
    
    private AbstractCompileMojo mojo;
    
    public Javah() {
    }

	public void setAbstractCompileMojo(AbstractCompileMojo mojo) {
		this.mojo = mojo;
	}
	
    protected List getClassPaths() throws MojoExecutionException {
        if (classPaths.isEmpty()) {
            try {
                classPaths.addAll(mojo.getMavenProject().getCompileClasspathElements());
            } catch (DependencyResolutionRequiredException e) {
                throw new MojoExecutionException("JAVAH, cannot get classpath", e);
            }
        }
        return classPaths;
    }

    protected File getJniDirectory() {
        if (jniDirectory == null) {
            jniDirectory = new File(mojo.getMavenProject().getBuild().getDirectory(), "nar/javah-include");
        }
        return jniDirectory;
    }
    
    protected File getClassDirectory() {
        if (classDirectory == null) {
            classDirectory = new File(mojo.getMavenProject().getBuild().getDirectory(), "classes");
        }
        return classDirectory;
    }
    
    protected Set getIncludes() {
        if (includes.isEmpty()) {
            includes.add("**/*.class");
        }
        return includes;
    }
    
    protected File getTimestampDirectory() {
        if (timestampDirectory == null) {
            timestampDirectory = getJniDirectory();
        }
        return timestampDirectory;
    }

    protected File getTimestampFile() {
        if (timestampFile == null) {
            timestampFile = new File(name);
        }
        return timestampFile; 
    }
        
    public void execute() throws MojoExecutionException {
        getClassDirectory().mkdirs();
        
        try {        
            SourceInclusionScanner scanner = new StaleSourceScanner(staleMillis, getIncludes(), excludes);
            if (getTimestampDirectory().exists()) {
                scanner.addSourceMapping(new SingleTargetSourceMapping( ".class", getTimestampFile().getPath() ));
            } else {
                scanner.addSourceMapping(new SuffixMapping( ".class", ".dummy" ));
            }

            Set classes = scanner.getIncludedSources(getClassDirectory(), getTimestampDirectory());
                    
            if (!classes.isEmpty()) {
                Set files = new HashSet();
                for (Iterator i=classes.iterator(); i.hasNext(); ) {
                    String file = ((File)i.next()).getPath();
                    JavaClass clazz = NarUtil.getBcelClass(file);
                    Method[] method = clazz.getMethods();
                    for (int j=0; j<method.length; j++) {
                        if (method[j].isNative()) files.add(clazz.getClassName()); 
                    }
                }
                
                if (!files.isEmpty()) {
                    getJniDirectory().mkdirs();
                    getTimestampDirectory().mkdirs();

                    mojo.getLog().info( "Running "+name+" compiler on "+files.size()+" classes...");
                    NarUtil.runCommand(generateCommandLine(files), null, mojo.getLog());
                    FileUtils.fileWrite(getTimestampDirectory()+"/"+getTimestampFile(), "");
                }
            }
        } catch (InclusionScanException e) {
            throw new MojoExecutionException( "JAVAH: Class scanning failed", e );
        } catch (IOException e) {
            throw new MojoExecutionException( "JAVAH: IO Exception", e );
        } catch (ClassFormatException e) {
        	throw new MojoExecutionException( "JAVAH: Class could not be inspected", e);
        }
    }

    private String[] generateCommandLine(Set/*<String>*/ classes) throws MojoExecutionException {
        
        List cmdLine = new ArrayList();
        
        cmdLine.add(name);
        
        if (!bootClassPaths.isEmpty()) {
            cmdLine.add("-bootclasspath");        
            cmdLine.add(StringUtils.join(bootClassPaths.iterator(), File.pathSeparator));
        }
        
        cmdLine.add("-classpath");
        cmdLine.add(StringUtils.join(getClassPaths().iterator(), File.pathSeparator));
        
        cmdLine.add("-d");
        cmdLine.add(getJniDirectory().getPath());        
        
        if (mojo.getLog().isDebugEnabled()) {
            cmdLine.add("-verbose");
        }
    
        if (classes != null) {
            for (Iterator i = classes.iterator(); i.hasNext(); ) {
                cmdLine.add((String)i.next());
            }
        }
        
        mojo.getLog().debug(cmdLine.toString());
        
        return (String[])cmdLine.toArray(new String[cmdLine.size()]);
    }        
}

