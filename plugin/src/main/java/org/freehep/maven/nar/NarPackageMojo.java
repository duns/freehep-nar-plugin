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
 * @version $Id: plugin/src/main/java/org/freehep/maven/nar/NarPackageMojo.java c867ab546be1 2007/07/05 21:26:30 duns $
 */
public class NarPackageMojo extends AbstractCompileMojo {

	/**
	 * Used for attaching the artifact in the project
	 * 
	 * @component
	 */
	private MavenProjectHelper projectHelper;

	public void execute() throws MojoExecutionException, MojoFailureException {
		if (shouldSkip())
			return;

		NarInfo info = new NarInfo(getMavenProject().getGroupId(),
				getMavenProject().getArtifactId(), getMavenProject()
						.getVersion(), getLog());

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

		File narDirectory = new File(getOutputDirectory(), "nar");

		// noarch
		String include = "include";
		if (new File(narDirectory, include).exists()) {
			File noarchFile = new File(getOutputDirectory(), getFinalName()
					+ "-" + NAR_NO_ARCH + "." + NAR_EXTENSION);
			nar(noarchFile, narDirectory, new String[] { include });
			projectHelper.attachArtifact(getMavenProject(), NAR_TYPE,
					NAR_NO_ARCH, noarchFile);
			info.setNar(null, "noarch", getMavenProject().getGroupId() + ":"
					+ getMavenProject().getArtifactId() + ":" + NAR_TYPE + ":"
					+ NAR_NO_ARCH);
		}

		String bindingType = null;
		for (Iterator i = getLibraries().iterator(); i.hasNext();) {
			Library library = (Library) i.next();
			String type = library.getType();
			if (bindingType == null)
				bindingType = type;
			// aol
			String bin = "bin";
			String lib = "lib";
			if (new File(narDirectory, bin).exists()
					|| new File(narDirectory, lib).exists()) {
				// aol
				File archFile = new File(getOutputDirectory(), getFinalName()
						+ "-" + getAOL() + "-" + type + "." + NAR_EXTENSION);
				nar(archFile, narDirectory, new String[] { bin, lib });
				projectHelper.attachArtifact(getMavenProject(), NAR_TYPE,
						getAOL() + "-" + type, archFile);
				info.setNar(null, type, getMavenProject().getGroupId() + ":"
						+ getMavenProject().getArtifactId() + ":" + NAR_TYPE
						+ ":" + "${aol}-" + type);
			}
		}

		// FIXME hardcoded JNI as default
		info.setBinding(null, bindingType != null ? bindingType : Library.JNI);

		try {
			info.writeToFile(propertiesFile);
		} catch (IOException ioe) {
			throw new MojoExecutionException(
					"Cannot write nar properties file", ioe);
		}
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
