// Copyright FreeHEP, 2006-2007.
package org.freehep.maven.nar;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;

/**
 * Assemble libraries of NAR files.
 * 
 * @goal nar-assembly
 * @phase process-resources
 * @requiresProject
 * @requiresDependencyResolution
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: src/main/java/org/freehep/maven/nar/NarAssemblyMojo.java 257950754a8f 2007/01/05 19:33:00 duns $
 */
public class NarAssemblyMojo extends AbstractDependencyMojo {

	/**
	 * List of classifiers which you want to assemble. Example ppc-MacOSX-g++,
	 * x86-Windows-msvc, i386-Linux-g++.
	 * 
	 * @parameter expression=""
	 * @required
	 */
	private List classifiers;

	public void execute() throws MojoExecutionException, MojoFailureException {
		if (shouldSkip()) {
    		getLog().info("***********************************************************************");
    		getLog().info("NAR Assembly SKIPPED since no NAR libraries were built/downloaded.");
    		getLog().info("***********************************************************************");
    		// NOTE: continue since the standard assemble mojo fails if we do not create the directories...
		}

		for (Iterator j = classifiers.iterator(); j.hasNext();) {
			String classifier = (String) j.next();

			// FIXME, hardcoded
			String[] types = { "jni", "shared" };

			for (int t = 0; t < types.length; t++) {
				List narArtifacts = getNarManager().getNarDependencies(
						"compile");
				List dependencies = getNarManager().getAttachedNarDependencies(
						narArtifacts, classifier, types[t]);
				for (Iterator d = dependencies.iterator(); d.hasNext();) {
					Artifact dependency = (Artifact) d.next();
					getLog().debug("Assemble from " + dependency);

					String prefix = classifier.replace("-", ".") + ".";

					// FIXME reported to maven developer list, isSnapshot
					// changes behaviour
					// of getBaseVersion, called in pathOf.
					if (dependency.isSnapshot())
						;
					File srcDir = new File(getLocalRepository().pathOf(dependency));
					srcDir = new File(getLocalRepository().getBasedir(), srcDir
							.getParent());
                    srcDir = new File(srcDir, "nar/lib/"
                            + classifier
                            + "/"
                            + types[t]
                            + "/");
					File dst = new File("target/nar/lib/" + classifier + "/"
							+ types[t]);
					try {
                        FileUtils.mkdir(dst.getPath());
                        if (shouldSkip()) {
                            File note = new File(dst, "NAR_ASSEMBLY_SKIPPED");
                            FileUtils.fileWrite(note.getPath(), 
                                "The NAR Libraries of this distribution are missing because \n"+
                                "the NAR dependencies were not built/downloaded, presumably because\n"+
                                "the the distribution was built with the '-Dnar.skip=true' flag."
                            );
                        } else {                        
                            getLog().debug("SrcDir: "+srcDir);
                            if (srcDir.exists()) {
                                List fileNames = FileUtils.getFileNames(srcDir, "*.so,*.a,*.dll,*.lib,*.jnilib", "", true, false);
                                for (Iterator f = fileNames.iterator(); f.hasNext(); ) {
                                    String fileName = (String)f.next();
                                    FileUtils.copyFileToDirectory(new File(fileName), dst);
                                }
                            }
						}
					} catch (IOException ioe) {
						throw new MojoExecutionException("Failed to copy files from dependency "
								+ dependency + " to " + dst, ioe);
					}
				}
			}
		}
	}
}
