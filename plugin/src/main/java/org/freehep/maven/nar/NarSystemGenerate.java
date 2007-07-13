// Copyright FreeHEP, 2007.
package org.freehep.maven.nar;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Generates a NarSystem class with static methods to use inside the java part
 * of the library.
 * 
 * @goal nar-system-generate
 * @phase generate-sources
 * @requiresProject
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: plugin/src/main/java/org/freehep/maven/nar/NarSystemGenerate.java eda4d0bbde3d 2007/07/03 16:52:10 duns $
 */
public class NarSystemGenerate extends AbstractCompileMojo {

	/**
	 * Name of the NarSystem class
	 * 
	 * @parameter expression="NarSystem"
	 * @required
	 */
	private String narSystemName;

	/**
	 * The target directory into which to generate the output.
	 * 
	 * @parameter expression="${project.build.directory}/nar/nar-generated"
	 * @required
	 */
	private File narGenerated;

	public void execute() throws MojoExecutionException, MojoFailureException {
		if (shouldSkip())
			return;

		// get packageName if specified for JNI.
		String packageName = null;
		for (Iterator i = getLibraries().iterator(); i.hasNext()
				&& (packageName == null);) {
			Library library = (Library) i.next();
			if (library.getType().equals(Library.JNI)) {
				packageName = library.getPackageName();
			}
		}

		if (packageName == null)
			return;

		// make sure destination is there
		narGenerated.mkdirs();

		getMavenProject().addCompileSourceRoot(narGenerated.getPath());

		File fullDir = new File(narGenerated, packageName.replace('.', '/'));
		fullDir.mkdirs();

		File narSystem = new File(fullDir, narSystemName + ".java");
		try {
			PrintWriter p = new PrintWriter(narSystem);
			p.println("// DO NOT EDIT: Generated by NarSystemGenerate.");
			p.println("package " + packageName + ";");
			p.println("");
			p.println("public class NarSystem {");
			p.println("");
			p.println("    private NarSystem() {");
			p.println("    }");
			p.println("");
			p.println("    public static void loadLibrary() {");
			p.println("        System.loadLibrary(\""
					+ getMavenProject().getArtifactId() + "-"
					+ getMavenProject().getVersion() + "\");");
			p.println("    }");
			p.println("}");
			p.close();
		} catch (IOException e) {
			throw new MojoExecutionException("Could not write '"
					+ narSystemName + "'", e);
		}
	}
}