// Copyright FreeHEP, 2006.
package org.freehep.maven.nar;

import java.util.Properties;

/**
 * 
 * @author Mark Donszelmann
 * @version $Id: src/main/java/org/freehep/maven/nar/NarInfo.java d3e5b1ffc9be 2006/06/15 22:00:33 duns $
 */
public class NarInfo {

	private String groupId;
	private String artifactId;
	private String version;
	private Properties info;
	
	public NarInfo(String groupId, String artifactId, String version, Properties info) {
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
		this.info = info;
	}

	public String getBinding(String aol) {
		return getProperty(aol, "libs.binding", "static");
	}

	// FIXME replace with list of AttachedNarArtifacts
	public String[] getAttachedNars(String aol, String type) {
		return getProperty(aol, "nar."+type, "").split(",");
	}

	public String getAOL(String aol) {
		return getProperty(aol, aol, aol);
	}

	public String getLibs(String aol) {
		return getProperty(aol, "libs.names", artifactId+"-"+version);
	}
	
	public String getSysLibs(String aol) {
		return getProperty(aol, "syslibs.names", null);
	}

	private String getProperty(String aol, String key, String defaultValue) {
		String value = info.getProperty(key, defaultValue);
		return info.getProperty(aol+"."+key, value);
	}
}
