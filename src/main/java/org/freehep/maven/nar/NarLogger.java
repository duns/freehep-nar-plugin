// Copyright FreeHEP, 2005.
package org.freehep.maven.nar;

import org.apache.maven.plugin.logging.Log;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Project;

/**
 * Logger to connect the Ant logging to the Maven logging.
 *
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: src/main/java/org/freehep/maven/nar/NarLogger.java bcdae088c368 2005/11/19 07:52:18 duns $
 */
public class NarLogger implements BuildListener {
    
    private Log log;
    
    public NarLogger(Log log) {
        this.log = log;
    }
    
    public void buildStarted(BuildEvent event) {
        System.err.println("bs "+event);
    }
    
    public void buildFinished(BuildEvent event) {
        System.err.println("bf "+event);
    }
    
    public void targetStarted(BuildEvent event) {
        System.err.println("gs "+event);
    }
    
    public void targetFinished(BuildEvent event) {
        System.err.println("gf "+event);
    }
    
    public void taskStarted(BuildEvent event) {
        System.err.println("ts "+event);
    }
    
    public void taskFinished(BuildEvent event) {
        System.err.println("tf "+event);
    }

    public void messageLogged(BuildEvent event) {
//                System.err.println("m "+event.getPriority()+" "+event.getMessage());
        switch (event.getPriority()) {
            case Project.MSG_ERR:
                log.error(event.getMessage());
                break;    
            case Project.MSG_WARN:
                log.warn(event.getMessage());
                break;    
            case Project.MSG_INFO:
                log.info(event.getMessage());
                break;    
            case Project.MSG_VERBOSE:
                log.info(event.getMessage());
                break;    
            default:    
            case Project.MSG_DEBUG:
                log.debug(event.getMessage());
                break;
        }
    }
}
