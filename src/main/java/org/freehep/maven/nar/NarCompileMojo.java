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
import net.sf.antcontrib.cpptasks.types.SystemLibrarySet;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

/**
 * Compiles native source files.
 * 
 * @goal nar-compile
 * @phase compile
 * @requiresDependencyResolution compile
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: src/main/java/org/freehep/maven/nar/NarCompileMojo.java 8c1595ae1e05 2006/10/13 23:26:37 duns $
 */
public class NarCompileMojo extends AbstractCompileMojo {

	public void execute() throws MojoExecutionException, MojoFailureException {
		if (shouldSkip()) {
			getLog().warn("NAR Plugin running is SKIPPED.");
			return;
		}

		// make sure destination is there
		getTargetDirectory().mkdirs();

		for (Iterator i = getLibraries().iterator(); i.hasNext();) {
			createLibrary(getAntProject(), (Library) i.next());
		}

		try {
			// FIXME, should the include paths be defined at a higher level ?
			getCpp().copyIncludeFiles(getMavenProject(),
					new File(getTargetDirectory(), "include"));
		} catch (IOException e) {
			throw new MojoExecutionException(
					"NAR: could not copy include files", e);
		}
	}

	private void createLibrary(Project antProject, Library library)
			throws MojoExecutionException, MojoFailureException {
		// configure task
		CCTask task = new CCTask();
		task.setProject(antProject);

		// outtype
		OutputTypeEnum outTypeEnum = new OutputTypeEnum();
		String type = library.getType();
		outTypeEnum.setValue(type);
		task.setOuttype(outTypeEnum);

		// std c++
		task.setLinkCPP(library.linkCPP());

		// outDir
		File outDir = new File(getTargetDirectory(), "lib");
		outDir = new File(outDir, getAOL());
		outDir = new File(outDir, type);
		outDir.mkdirs();

		// outFile
		File outFile = new File(outDir, getOutput());
		if (getLogLevel() >= LOG_LEVEL_INFO) getLog().info("NAR - output: '" + outFile + "'");
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
		task.addConfiguredCompiler(getCpp().getCompiler(getMavenProject(),
				antProject, getOS(), getAOLKey(), type, getOutput()));

		// add C compiler
		// FIXME use this as param
		task.addConfiguredCompiler(getC().getCompiler(getMavenProject(),
				antProject, getOS(), getAOLKey(), type, getOutput()));

		// add Fortran compiler
		// FIXME use this as param
		task.addConfiguredCompiler(getFortran().getCompiler(getMavenProject(),
				antProject, getOS(), getAOLKey(), type, getOutput()));

		// add javah include path
		File jniDirectory = getJavah().getJniDirectory(getMavenProject());
		if (jniDirectory.exists())
			task.createIncludePath().setPath(jniDirectory.getPath());

		// add java include paths
		// FIXME, get rid of task
		getJava().addIncludePaths(getMavenProject(), task, this, type);

		// add dependency include paths
		for (Iterator i = getNarManager().getNarDependencies("compile")
				.iterator(); i.hasNext();) {
			// FIXME, handle multiple includes from one NAR
			NarArtifact narDependency = (NarArtifact) i.next();
			String binding = narDependency.getNarInfo().getBinding(getAOL(),
					"static");
			if (!binding.equals("jni")) {
				File include = new File(getNarManager().getNarFile(
						narDependency).getParentFile(), "nar/include");
				if (include.exists()) {
					task.createIncludePath().setPath(include.getPath());
				}
			}
		}

		// add linker
		task.addConfiguredLinker(getLinker().getLinker(this, antProject,
				getOS(), getAOLKey() + "linker.", type));

		// add dependency libraries
		if (type.equals("shared") || type.equals("jni")) {
			for (Iterator i = getNarManager().getNarDependencies("compile")
					.iterator(); i.hasNext();) {
				NarArtifact dependency = (NarArtifact) i.next();

				// FIXME no handling of "local"

				// FIXME, no way to override this at this stage
				String binding = dependency.getNarInfo().getBinding(getAOL(),
						"static");
//				System.err.println("BINDING " + binding);
				String aol = getAOL();
				aol = dependency.getNarInfo().getAOL(getAOL());
//				System.err.println("LIB AOL " + aol);

				if (!binding.equals("jni")) {
					File dir = new File(getNarManager().getNarFile(dependency)
							.getParentFile(), "nar/lib/" + aol + "/" + binding);
//					System.err.println("LIB DIR " + dir);
					if (dir.exists()) {
						LibrarySet libSet = new LibrarySet();
						libSet.setProject(antProject);

						// FIXME, no way to override
						String libs = dependency.getNarInfo().getLibs(getAOL());
//						System.err.println("LIBS = " + libs);
						libSet.setLibs(new CUtil.StringArrayBuilder(libs));
						libSet.setDir(dir);
						task.addLibset(libSet);
					} else {
//						System.err.println("LIB DIR " + dir
//								+ " does NOT exist.");
					}

					String sysLibs = dependency.getNarInfo().getSysLibs(
							getAOL());
					if (sysLibs != null) {
//						System.err.println("SYSLIBS = " + sysLibs);
						SystemLibrarySet sysLibSet = new SystemLibrarySet();
						sysLibSet.setProject(antProject);

						sysLibSet
								.setLibs(new CUtil.StringArrayBuilder(sysLibs));
						task.addSyslibset(sysLibSet);
					}
				}
			}
		}

		// Add JVM to linker
		if (!getOS().equals("MacOSX")) {
			// FIXME, use "this".
			getJava().addRuntime(antProject, task, getJavaHome(),
					getAOLKey() + "java.");
		}

		// execute
		try {
			task.execute();
		} catch (BuildException e) {
			throw new MojoExecutionException("NAR: Compile failed", e);
		}
	}
}
