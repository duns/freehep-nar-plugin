// Copyright FreeHEP, 2005-2007.
package org.freehep.maven.nar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.antcontrib.cpptasks.CUtil;
import net.sf.antcontrib.cpptasks.CompilerDef;
import net.sf.antcontrib.cpptasks.CompilerEnum;
import net.sf.antcontrib.cpptasks.OptimizationEnum;
import net.sf.antcontrib.cpptasks.types.CompilerArgument;
import net.sf.antcontrib.cpptasks.types.ConditionalFileSet;
import net.sf.antcontrib.cpptasks.types.DefineArgument;
import net.sf.antcontrib.cpptasks.types.DefineSet;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.StringUtils;

/**
 * Abstract Compiler class
 * 
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: plugin/src/main/java/org/freehep/maven/nar/Compiler.java 3ac1d2951571 2007/07/10 21:53:48 duns $
 */
public abstract class Compiler {

	/**
	 * The name of the compiler Some choices are: "msvc", "g++", "gcc", "CC",
	 * "cc", "icc", "icpc", ... Default is Architecture-OS-Linker specific:
	 * FIXME: table missing
	 * 
	 * @parameter expression=""
	 */
	private String name;

	/**
	 * Source directory for native files
	 * 
	 * @parameter expression="${basedir}/src/main"
	 * @required
	 */
	private File sourceDirectory;

	/**
	 * Include patterns for sources
	 * 
	 * @parameter expression=""
	 * @required
	 */
	private Set includes = new HashSet();

	/**
	 * Exclude patterns for sources
	 * 
	 * @parameter expression=""
	 * @required
	 */
	private Set excludes = new HashSet();

	/**
	 * Compile with debug information.
	 * 
	 * @parameter expression="" default-value="false"
	 * @required
	 */
	private boolean debug = false;

	/**
	 * Enables generation of exception handling code.
	 * 
	 * @parameter expression="" default-value="true"
	 * @required
	 */
	private boolean exceptions = true;

	/**
	 * Enables run-time type information.
	 * 
	 * @parameter expression="" default-value="true"
	 * @required
	 */
	private boolean rtti = true;

	/**
	 * Sets optimization. Possible choices are: "none", "size", "minimal",
	 * "speed", "full", "aggressive", "extreme", "unsafe".
	 * 
	 * @parameter expression="" default-value="none"
	 * @required
	 */
	private String optimize = "none";

	/**
	 * Enables or disables generation of multi-threaded code. Default value:
	 * false, except on Windows.
	 * 
	 * @parameter expression="" default-value="false"
	 * @required
	 */
	private boolean multiThreaded = false;

	/**
	 * Defines
	 * 
	 * @parameter expression=""
	 */
	private List defines;

	/**
	 * Clears default defines
	 * 
	 * @parameter expression="" default-value="false"
	 * @required
	 */
	private boolean clearDefaultDefines;

	/**
	 * Undefines
	 * 
	 * @parameter expression=""
	 */
	private List undefines;

	/**
	 * Clears default undefines
	 * 
	 * @parameter expression="" default-value="false"
	 * @required
	 */
	private boolean clearDefaultUndefines;

	/**
	 * Include Paths. Defaults to "${sourceDirectory}/include"
	 * 
	 * @parameter expression=""
	 */
	private List includePaths;

	/**
	 * System Include Paths, which are added at the end of all include paths
	 * 
	 * @parameter expression=""
	 */
	private List systemIncludePaths;

	/**
	 * Additional options for the C++ compiler Defaults to
	 * Architecture-OS-Linker specific values. FIXME table missing
	 * 
	 * @parameter expression=""
	 */
	private List options;

	/**
	 * Clears default options
	 * 
	 * @parameter expression="" default-value="false"
	 * @required
	 */
	private boolean clearDefaultOptions;

	protected File getSourceDirectory(MavenProject mavenProject, String type) {
		if (sourceDirectory == null) {
			sourceDirectory = new File(mavenProject.getBasedir(), "src/"
					+ (type.equals("test") ? "test" : "main"));
		}
		return sourceDirectory;
	}

	protected List/* <String> */getIncludePaths(MavenProject mavenProject,
			String type) {
		if (includePaths == null || (includePaths.size() == 0)) {
			includePaths = new ArrayList();
			includePaths.add(new File(getSourceDirectory(mavenProject, type),
					"include").getPath());
		}
		return includePaths;
	}

	public CompilerDef getCompiler(AbstractCompileMojo mojo, String type,
			String output) throws MojoFailureException {

		String prefix = mojo.getAOLKey() + getName() + ".";

		// adjust default values
		if (name == null)
			name = NarUtil.getDefaults().getProperty(prefix + "compiler");
		if (name == null) {
			throw new MojoFailureException(
					"NAR: Please specify <Name> as part of <Cpp>, <C> or <Fortran> for "
							+ prefix);
		}

		Set finalIncludes = new HashSet();
		Set finalExcludes = new HashSet();
		if (!type.equals("test")) {
			// add all includes and excludes
			if (includes.isEmpty()) {
				String defaultIncludes = NarUtil.getDefaults().getProperty(
						prefix + "includes");
				if (defaultIncludes == null) {
					throw new MojoFailureException(
							"NAR: Please specify <Includes> as part of <Cpp>, <C> or <Fortran> for "
									+ prefix);
				}
				String[] include = defaultIncludes.split(" ");
				for (int i = 0; i < include.length; i++) {
					finalIncludes.add(include[i].trim());
				}
			} else {
				finalIncludes.addAll(includes);
			}
			if (excludes.isEmpty()) {
				String defaultExcludes = NarUtil.getDefaults().getProperty(
						prefix + "excludes");
				if (defaultExcludes != null) {
					String[] exclude = defaultExcludes.split(" ");
					for (int i = 0; i < exclude.length; i++) {
						finalExcludes.add(exclude[i].trim());
					}
				}
			} else {
				finalExcludes.addAll(excludes);
			}
		} else {
			String defaultIncludes = NarUtil.getDefaults().getProperty(
					prefix + "includes");
			if (defaultIncludes == null) {
				throw new MojoFailureException(
						"NAR: Please specify <Includes> as part of <Cpp>, <C> or <Fortran> for "
								+ prefix);
			}

			String[] include = defaultIncludes.split(" ");
			for (int i = 0; i < include.length; i++) {
				finalIncludes.add(include[i].trim());
			}

			if (excludes.isEmpty()) {
				String defaultExcludes = NarUtil.getDefaults().getProperty(
						prefix + "excludes");
				if (defaultExcludes != null) {
					String[] exclude = defaultExcludes.split(" ");
					for (int i = 0; i < exclude.length; i++) {
						finalExcludes.add(exclude[i].trim());
					}
				}
			} else {
				finalExcludes.addAll(excludes);
			}

			// now add all but the current test to the excludes
			for (Iterator i = mojo.getTests().iterator(); i.hasNext();) {
				Test test = (Test) i.next();
				if (!test.getName().equals(output)) {
					finalExcludes.add("**/" + test.getName() + ".*");
				}
			}
		}

		CompilerDef compiler = new CompilerDef();
		compiler.setProject(mojo.getAntProject());
		CompilerEnum compilerName = new CompilerEnum();
		compilerName.setValue(name);
		compiler.setName(compilerName);

		// debug, exceptions, rtti, multiThreaded
		compiler.setDebug(debug);
		compiler.setExceptions(exceptions);
		compiler.setRtti(rtti);
		compiler.setMultithreaded(mojo.getOS().equals("Windows") ? true
				: multiThreaded);

		// optimize
		OptimizationEnum optimization = new OptimizationEnum();
		optimization.setValue(optimize);
		compiler.setOptimize(optimization);

		// add options
		if (options != null) {
			for (Iterator i = options.iterator(); i.hasNext();) {
				CompilerArgument arg = new CompilerArgument();
				arg.setValue((String) i.next());
				compiler.addConfiguredCompilerArg(arg);
			}
		}

		if (!clearDefaultOptions) {
			String optionsProperty = NarUtil.getDefaults().getProperty(
					prefix + "options");
			if (optionsProperty != null) {
				String[] option = optionsProperty.split(" ");
				for (int i = 0; i < option.length; i++) {
					CompilerArgument arg = new CompilerArgument();
					arg.setValue(option[i]);
					compiler.addConfiguredCompilerArg(arg);
				}
			}
		}

		// add defines
		if (defines != null) {
			DefineSet defineSet = new DefineSet();
			for (Iterator i = defines.iterator(); i.hasNext();) {
				DefineArgument define = new DefineArgument();
				String[] pair = ((String) i.next()).split("=", 2);
				define.setName(pair[0]);
				define.setValue(pair.length > 1 ? pair[1] : null);
				defineSet.addDefine(define);
			}
			compiler.addConfiguredDefineset(defineSet);
		}

		if (!clearDefaultDefines) {
			DefineSet defineSet = new DefineSet();
			String defaultDefines = NarUtil.getDefaults().getProperty(
					prefix + "defines");
			if (defaultDefines != null) {
				defineSet
						.setDefine(new CUtil.StringArrayBuilder(defaultDefines));
			}
			compiler.addConfiguredDefineset(defineSet);
		}

		// add undefines
		if (undefines != null) {
			DefineSet undefineSet = new DefineSet();
			for (Iterator i = undefines.iterator(); i.hasNext();) {
				DefineArgument undefine = new DefineArgument();
				String[] pair = ((String) i.next()).split("=", 2);
				undefine.setName(pair[0]);
				undefine.setValue(pair.length > 1 ? pair[1] : null);
				undefineSet.addUndefine(undefine);
			}
			compiler.addConfiguredDefineset(undefineSet);
		}

		if (!clearDefaultUndefines) {
			DefineSet undefineSet = new DefineSet();
			String defaultUndefines = NarUtil.getDefaults().getProperty(
					prefix + "undefines");
			if (defaultUndefines != null) {
				undefineSet.setUndefine(new CUtil.StringArrayBuilder(
						defaultUndefines));
			}
			compiler.addConfiguredDefineset(undefineSet);
		}

		// add include path
		for (Iterator i = getIncludePaths(mojo.getMavenProject(), type)
				.iterator(); i.hasNext();) {
			String path = (String) i.next();
			compiler.createIncludePath().setPath(path);
		}

		// add system include path (at the end)
		if (systemIncludePaths != null) {
			for (Iterator i = systemIncludePaths.iterator(); i.hasNext();) {
				String path = (String) i.next();
				compiler.createSysIncludePath().setPath(path);
			}
		}

		// Add default fileset (if exists)
		File srcDir = getSourceDirectory(mojo.getMavenProject(), type);
		mojo.getLog().debug(
				"Checking for existence of " + getName() + " sourceDirectory: "
						+ srcDir);
		if (srcDir.exists()) {
			ConditionalFileSet fileSet = new ConditionalFileSet();
			fileSet.setProject(mojo.getAntProject());
			fileSet
					.setIncludes(StringUtils
							.join(finalIncludes.iterator(), ","));
			fileSet
					.setExcludes(StringUtils
							.join(finalExcludes.iterator(), ","));
			fileSet.setDir(srcDir);
			compiler.addFileset(fileSet);
		}

		// add other sources
		for (Iterator i = mojo.getMavenProject().getCompileSourceRoots()
				.iterator(); i.hasNext();) {
			File dir = new File((String) i.next());
			mojo.getLog().debug(
					"Checking for existence of " + getName()
							+ " sourceCompileRoot: " + dir);
			if (dir.exists()) {
				ConditionalFileSet otherFileSet = new ConditionalFileSet();
				otherFileSet.setProject(mojo.getAntProject());
				otherFileSet.setIncludes(StringUtils.join(finalIncludes
						.iterator(), ","));
				otherFileSet.setExcludes(StringUtils.join(finalExcludes
						.iterator(), ","));
				otherFileSet.setDir(dir);
				compiler.addFileset(otherFileSet);
			}
		}
		return compiler;
	}

	protected abstract String getName();

	public void copyIncludeFiles(MavenProject mavenProject, File targetDirectory)
			throws IOException {
		for (Iterator i = getIncludePaths(mavenProject, "dummy").iterator(); i
				.hasNext();) {
			File path = new File((String) i.next());
			if (path.exists()) {
				NarUtil.copyDirectoryStructure(path, targetDirectory, null,
						NarUtil.DEFAULT_EXCLUDES);
			}
		}
	}
}
