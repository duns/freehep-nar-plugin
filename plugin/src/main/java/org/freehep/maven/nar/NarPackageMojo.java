// Copyright FreeHEP, 2005-2007.
package org.freehep.maven.nar;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.zip.ZipArchiver;

/**
 * Jars up the NAR files.
 * 
 * @goal nar-package
 * @phase package
 * @requiresProject
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: plugin/src/main/java/org/freehep/maven/nar/NarPackageMojo.java f934ad2b8948 2007/07/13 14:17:10 duns $
 */
public class NarPackageMojo extends AbstractCompileMojo {

	/**
	 * Used for attaching the artifact in the project
	 * 
	 * @component
	 */
	private MavenProjectHelper projectHelper;

	private File narDirectory;
	private NarInfo info;

	public void execute() throws MojoExecutionException, MojoFailureException {
		if (shouldSkip())
			return;

		narDirectory = new File(getOutputDirectory(), "nar");

		info = new NarInfo(getMavenProject().getGroupId(), getMavenProject()
				.getArtifactId(), getMavenProject().getVersion(), getLog());

		// General properties.nar file
		File propertiesDir = new File(getOutputDirectory(),
				"classes/META-INF/nar/" + getMavenProject().getGroupId() + "/"
						+ getMavenProject().getArtifactId());
		if (!propertiesDir.exists()) {
			propertiesDir.mkdirs();
		}
		File propertiesFile = new File(propertiesDir, NarInfo.NAR_PROPERTIES);
		try {
			info.read(propertiesFile);
		} catch (IOException ioe) {
			// ignored
		}

		// noarch
		String include = "include";
		if (new File(narDirectory, include).exists()) {
			attachNar("include", null, NAR_NO_ARCH);
		}

		// create nar with binaries
		String bin = "bin";
		String[] binAOLs = new File(narDirectory, bin).list();
		for (int i = 0; i < (binAOLs != null ? binAOLs.length : 0); i++) {
			attachNar(bin + "/" + binAOLs[i], binAOLs[i], bin);
		}

		// create nars for each type of library (static, shared).
		String bindingType = null;
		for (Iterator i = getLibraries().iterator(); i.hasNext();) {
			Library library = (Library) i.next();
			String type = library.getType();
			if (bindingType == null)
				bindingType = type;

			// create nar with libraries
			String lib = "lib";
			String[] libAOLs = new File(narDirectory, lib).list();
			for (int j = 0; j < (libAOLs != null ? libAOLs.length : 0); j++) {
				attachNar(lib + "/" + libAOLs[j] + "/" + type, libAOLs[j], type);
			}
		}

		info.setBinding(null, bindingType != null ? bindingType : Library.NONE);

		try {
			info.writeToFile(propertiesFile);
		} catch (IOException ioe) {
			throw new MojoExecutionException(
					"Cannot write nar properties file", ioe);
		}
	}

	private void attachNar(String dir, String aol, String type)
			throws MojoExecutionException {
		File libFile = new File(getOutputDirectory(), getFinalName() + "-"
				+ (aol != null ? aol + "-" : "") + type + "." + NAR_EXTENSION);
		nar(libFile, narDirectory, new String[] { dir });
		projectHelper.attachArtifact(getMavenProject(), NAR_TYPE,
				(aol != null ? aol + "-" : "") + type, libFile);
		info.setNar(null, type, getMavenProject().getGroupId() + ":"
				+ getMavenProject().getArtifactId() + ":" + NAR_TYPE + ":"
				+ (aol != null ? "${aol}-" : "") + type);

	}

	private void nar(File nar, File dir, String[] dirs)
			throws MojoExecutionException {
		try {
			if (nar.exists()) {
				nar.delete();
			}

			Archiver archiver = new ZipArchiver();
			// seems to return same archiver all the time
			// archiverManager.getArchiver(NAR_ROLE_HINT);
			for (int i = 0; i < dirs.length; i++) {
				String[] includes = new String[] { dirs[i] + "/**" };
				archiver.addDirectory(dir, includes, null);
			}
			archiver.setDestFile(nar);
			archiver.createArchive();
		} catch (ArchiverException e) {
			throw new MojoExecutionException(
					"Error while creating NAR archive.", e);
			// } catch (NoSuchArchiverException e) {
			// throw new MojoExecutionException("Error while creating NAR
			// archive.", e );
		} catch (IOException e) {
			throw new MojoExecutionException(
					"Error while creating NAR archive.", e);
		}
	}

}