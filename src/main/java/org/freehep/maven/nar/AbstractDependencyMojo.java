// Copyright FreeHEP, 2005-2006.
package org.freehep.maven.nar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: src/main/java/org/freehep/maven/nar/AbstractDependencyMojo.java fb2f54cb3103 2006/06/08 23:31:35 duns $
 */
public abstract class AbstractDependencyMojo extends AbstractNarMojo {

    /**
     * Returns those dependencies which are dependent on Nar files
     */
    protected List/*<Artifact>*/ getNarDependencies(String scope) throws MojoExecutionException {
        List narDependencies = new ArrayList();                
        for (Iterator i=getDependencies(scope).iterator(); i.hasNext(); ) {
            Artifact dependency = (Artifact)i.next();

            if (getNarProperties(dependency) != null) narDependencies.add(dependency);
        }
        return narDependencies;
    }
        
    /**
     * Returns all NAR dependencies, including noarch and aol.
     */
    protected List/*<Artifacts>*/ getAllNarDependencies(String scope) throws MojoExecutionException {
        List allNarDependencies = new ArrayList();
        for (Iterator i=getNarDependencies(scope).iterator(); i.hasNext(); ) {
            Artifact dependency = (Artifact)i.next();
            Properties properties = getNarProperties(dependency);
            String[] nars = properties.getProperty("nars", "").split(",");
            for (int j=0; j<nars.length; j++) {
                String[] nar = nars[j].split(":", 5);
                if (nar.length >= 4) {
                    try {
                        String groupId = nar[0].trim();
                        String artifactId = nar[1].trim();
                        String type = nar[2].trim();
                        String classifier = nar[3].trim();
                        if ("${aol}".equals(classifier)) {
                            try {
                                classifier = getAOL();
                                // translate for instance g++ to gcc...
                                classifier = properties.getProperty(classifier, classifier);
                            } catch (MojoFailureException e) {
                                throw new MojoExecutionException(e.getMessage(), e);
                            }
                        }
                        String version = nar.length >= 5 ? nar[4].trim() : dependency.getVersion();
                        allNarDependencies.add(new NarArtifact(groupId, artifactId, version, dependency.getScope(), type, classifier, dependency.isOptional()));
                    } catch (InvalidVersionSpecificationException e) {
                        throw new MojoExecutionException("Error while reading nar file for dependency " + dependency, e );
                    }                             
                } else {
                    getLog().warn("nars property contains invalid field: '"+nars[j]+"'");
                }
            }
        }
        return allNarDependencies;
    }

    protected File getNarFile(Artifact dependency) throws MojoFailureException {
        // FIXME reported to maven developer list, isSnapshot changes behaviour of getBaseVersion, called in pathOf.
        if (dependency.isSnapshot());
        return new File(localRepository.getBasedir(), localRepository.pathOf(dependency).replaceAll("\\$\\{aol\\}", getAOL()));
    }

    private List getDependencies(String scope) {
        if (scope.equals("test")) {
            return mavenProject.getTestArtifacts();
        } else if (scope.equals("runtime")) {
            return mavenProject.getRuntimeArtifacts();
        }
        return mavenProject.getCompileArtifacts();
     }
    
    private Properties getNarProperties(Artifact dependency) throws MojoExecutionException {
        // FIXME reported to maven developer list, isSnapshot changes behaviour of getBaseVersion, called in pathOf.
        if (dependency.isSnapshot());
        File file = new File(localRepository.getBasedir(), localRepository.pathOf(dependency));
        JarFile jar = null;
        Properties properties = null;
        try {
            jar = new JarFile(file);
            JarEntry entry = (JarEntry)jar.getEntry("META-INF/nar/"+dependency.getGroupId()+"/"+dependency.getArtifactId()+"/"+NAR_PROPERTIES);
            if (entry != null) {
                properties = new Properties();
                properties.load(jar.getInputStream(entry));
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Error while reading " + file, e );
        } finally {
            if (jar != null) {
                try {
                    jar.close();
                } catch (IOException e) {
                    //ignore
                }
            }
        }
        return properties;             
    }
}
