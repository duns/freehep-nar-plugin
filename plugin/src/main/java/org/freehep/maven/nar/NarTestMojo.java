// Copyright FreeHEP, 2005.
package org.freehep.maven.nar;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.tools.ant.Project;

/**
 * Tests NAR files. Runs Native Tests
 * 
 * @goal nar-test
 * @phase test
 * @requiresProject
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: plugin/src/main/java/org/freehep/maven/nar/NarTestMojo.java fc7c0e9b39c8 2007/07/04 16:50:32 duns $
 */
public class NarTestMojo extends AbstractCompileMojo {

	public void execute() throws MojoExecutionException, MojoFailureException {
		if (shouldSkip())
			return;

		for (Iterator i = getTests().iterator(); i.hasNext();) {
			runTest(getAntProject(), (Test) i.next());
		}
	}

	private void runTest(Project antProject, Test test)
			throws MojoExecutionException, MojoFailureException {
        // FIXME should move to NarTestMojo
        // run if requested
        if (test.shouldRun()) {
        	String name = getTargetDirectory()+"/bin/"+getAOL()+"/"+test.getLink()+"/"+test.getName();
            getLog().info( "Running "+name);
            // FIXME do something with return...
            int result = runCommand(generateCommandLine(name, getLog()), generateEnvironment(test, getLog()), getLog());
            if (result != 0) throw new MojoFailureException("Test "+name+" failed with exit code: "+result);
        }
	}

    protected File getTargetDirectory() {
        return new File(getMavenProject().getBuild().getDirectory(), "test-nar");
    }

	private String[] generateCommandLine(String name, Log log)
			throws MojoExecutionException {

		List cmdLine = new ArrayList();

		cmdLine.add(name);

		log.debug(cmdLine.toString());

		return (String[]) cmdLine.toArray(new String[cmdLine.size()]);
	}

	private String[] generateEnvironment(Test test, Log log) throws MojoFailureException {
		List env = new ArrayList();

		// FIXME, this should run over all produced libraries' types
		if (test.getLink().equals(Library.SHARED)) {
		    // link to our own library
			env.add("DYLD_LIBRARY_PATH="+getTargetDirectory()+"/"+"lib/"+getAOL()+"/"+test.getLink());
		}
		
		// FIXME add dependent libs
		
		log.info("Test Environment:");
		log.info(env.toString());
		
		return (String[]) env.toArray(new String[env.size()]);
	}

	// NOTE: same as in Javah.java
	private int runCommand(String[] cmdLine, String[] env, Log log)
			throws MojoExecutionException {
		try {
			Runtime runtime = Runtime.getRuntime();
			Process process = runtime.exec(cmdLine, env);
			StreamGobbler errorGobbler = new StreamGobbler(process
					.getErrorStream(), true, log);
			StreamGobbler outputGobbler = new StreamGobbler(process
					.getInputStream(), false, log);

			errorGobbler.start();
			outputGobbler.start();
			return process.waitFor();
		} catch (Throwable e) {
			throw new MojoExecutionException("Could not launch " + cmdLine[0],
					e);
		}
	}
	
	class StreamGobbler extends Thread {
		InputStream is;
		boolean error;
		Log log;

		StreamGobbler(InputStream is, boolean error, Log log) {
			this.is = is;
			this.error = error;
			this.log = log;
		}

		public void run() {
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));
				String line = null;
				while ((line = reader.readLine()) != null) {
					if (error) {
						log.error(line);
					} else {
						log.debug(line);
					}
				}
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
