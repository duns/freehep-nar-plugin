// Copyright FreeHEP, 2005.
package org.freehep.maven.nar;

import org.apache.tools.ant.Project;

/**
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: src/main/java/org/freehep/maven/nar/AbstractCompileMojo.java eec048018869 2005/11/18 06:31:36 duns $
 */
public abstract class AbstractCompileMojo extends AbstractDependencyMojo {

    private Project antProject;

    protected Project getAntProject() {
        if (antProject == null) {
            // configure ant project
            antProject = new Project();
            antProject.setName("NARProject");
            antProject.addBuildListener(new NarLogger(getLog()));
        }
        return antProject;
    }
}
