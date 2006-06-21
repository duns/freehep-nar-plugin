// Copyright FreeHEP, 2005-2006.
package org.freehep.maven.nar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: src/main/java/org/freehep/maven/nar/AbstractDependencyMojo.java f306842a5f50 2006/06/21 20:44:59 duns $
 */
public abstract class AbstractDependencyMojo extends AbstractNarMojo {

    private String[] narTypes = { "noarch", "static", "dynamic", "jni",
            "plugin" };

    /**
     * Returns dependencies which are dependent on NAR files (i.e. contain
     * NarInfo)
     */
    protected List/* <NarArtifact> */getNarDependencies(String scope)
            throws MojoExecutionException {
        List narDependencies = new ArrayList();
        for (Iterator i = getDependencies(scope).iterator(); i.hasNext();) {
            Artifact dependency = (Artifact) i.next();

            NarInfo narInfo = getNarInfo(dependency);
            if (narInfo != null) {
                narDependencies.add(new NarArtifact(dependency, narInfo));
            }
        }
        return narDependencies;
    }

    /**
     * Returns all NAR dependencies by type: noarch, static, dunamic, jni,
     * plugin.
     * 
     * @throws MojoFailureException
     */
    protected Map/* <String, List<AttachedNarArtifact>> */getAttachedNarDependencyMap(
            String scope) throws MojoExecutionException, MojoFailureException {
        Map attachedNarDependencies = new HashMap();
        for (Iterator i = getNarDependencies(scope).iterator(); i.hasNext();) {
            Artifact dependency = (Artifact) i.next();
            for (int j = 0; j < narTypes.length; j++) {
                List artifactList = getAttachedNarDependencies(dependency, getAOL(),
                        narTypes[j]);
                if (artifactList != null)
                    attachedNarDependencies.put(narTypes[j], artifactList);
            }
        }
        return attachedNarDependencies;
    }

    protected List/* <AttachedNarArtifact> */getAttachedNarDependencies(
            String scope) throws MojoExecutionException, MojoFailureException {
        return getAttachedNarDependencies(scope, getAOL());
    }
    
    /**
     * Returns a list of all attached nar dependencies for a specific binding
     * and "noarch", but not where "local" is specified
     * 
     * @param scope
     * @return
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    protected List/* <AttachedNarArtifact> */getAttachedNarDependencies(
            String scope, String aol) throws MojoExecutionException, MojoFailureException {
        List artifactList = new ArrayList();
        for (Iterator i = getNarDependencies(scope).iterator(); i.hasNext();) {
            Artifact dependency = (Artifact) i.next();
            System.err.println("Dep "+dependency);
            NarInfo narInfo = getNarInfo(dependency);
            artifactList
                    .addAll(getAttachedNarDependencies(dependency, null, "noarch"));

            // FIXME no handling of local
            artifactList.addAll(getAttachedNarDependencies(dependency, aol, narInfo
                    .getBinding(aol)));
        }
        return artifactList;
    }

    protected List/* <AttachedNarArtifact> */getAttachedNarDependencies(
            Artifact dependency, String aol, String narType) throws MojoExecutionException,
            MojoFailureException {
        List artifactList = new ArrayList();
        NarInfo narInfo = getNarInfo(dependency);
        String[] nars = narInfo.getAttachedNars(aol, narType);
        // FIXME Move this to info....
        if (nars != null) {
            for (int j = 0; j < nars.length; j++) {
                String[] nar = nars[j].split(":", 5);
                if (nar.length >= 4) {
                    try {
                        String groupId = nar[0].trim();
                        String artifactId = nar[1].trim();
                        String type = nar[2].trim();
                        String classifier = nar[3].trim();
                        // translate for instance g++ to gcc...
                        aol = narInfo.getAOL(aol);
                        if (aol != null) {
                            classifier = NarUtil.replace("${aol}", aol, classifier);
                        }
                        String version = nar.length >= 5 ? nar[4].trim()
                                : dependency.getVersion();
                        artifactList.add(new AttachedNarArtifact(groupId,
                                artifactId, version, dependency.getScope(), type,
                                classifier, dependency.isOptional()));
                    } catch (InvalidVersionSpecificationException e) {
                        throw new MojoExecutionException(
                                "Error while reading nar file for dependency "
                                        + dependency, e);
                    }
                } else {
                    getLog().warn(
                            "nars property in "+dependency.getArtifactId()+" contains invalid field: '" + nars[j]
                                    + "' for type: "+narType);
                }
            }
        }
        return artifactList;
    }

    protected File getNarFile(Artifact dependency) throws MojoFailureException {
        // FIXME reported to maven developer list, isSnapshot changes behaviour
        // of getBaseVersion, called in pathOf.
        if (dependency.isSnapshot())
            ;
        return new File(getLocalRepository().getBasedir(), NarUtil.replace("${aol}", getAOL(), getLocalRepository().pathOf(
                dependency)));
    }

    private List getDependencies(String scope) {
        if (scope.equals("test")) {
            return getMavenProject().getTestArtifacts();
        } else if (scope.equals("runtime")) {
            return getMavenProject().getRuntimeArtifacts();
        }
        return getMavenProject().getCompileArtifacts();
    }

    private NarInfo getNarInfo(Artifact dependency)
            throws MojoExecutionException {
        // FIXME reported to maven developer list, isSnapshot changes behaviour
        // of getBaseVersion, called in pathOf.
        if (dependency.isSnapshot())
            ;
        
        File file = new File(getLocalRepository().getBasedir(), getLocalRepository()
                .pathOf(dependency));
        JarFile jar = null;
        try {
            jar = new JarFile(file);
            NarInfo info = new NarInfo(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion());
            if (!info.exists(jar)) return null;
            info.read(jar);
            return info;
        } catch (IOException e) {
            throw new MojoExecutionException("Error while reading " + file, e);
        } finally {
            if (jar != null) {
                try {
                    jar.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }
}
