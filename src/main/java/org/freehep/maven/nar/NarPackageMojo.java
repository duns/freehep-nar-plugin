// Copyright FreeHEP, 2005-2006.
package org.freehep.maven.nar;

import java.io.File;
import java.io.IOException;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.zip.ZipArchiver;

/**
 * @description Jar up the NAR files.
 * @goal nar-package
 * @phase package
 * @requiresProject
 * @author <a href="Mark.Donszelmann@slac.stanford.edu">Mark Donszelmann</a>
 * @version $Id: src/main/java/org/freehep/maven/nar/NarPackageMojo.java 501cf4787202 2006/06/17 07:40:13 duns $
 */
public class NarPackageMojo extends AbstractNarMojo {
            
    public void execute() throws MojoExecutionException, MojoFailureException {
        // FIXME, multiple types should be possible
        String type = "jni";
        NarInfo info = new NarInfo(getMavenProject().getGroupId(), getMavenProject().getArtifactId(), getMavenProject().getVersion());

        // General properties.nar file
        File propertiesDir = new File(getOutputDirectory(), "classes/META-INF/nar/"+getMavenProject().getGroupId()+"/"+getMavenProject().getArtifactId());
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
            File noarchFile = new File(getOutputDirectory(), getFinalName()+"-"+NAR_NO_ARCH+"."+NAR_EXTENSION);
            nar(noarchFile, narDirectory, new String[] { include });
            addNarArtifact(NAR_TYPE, NAR_NO_ARCH, noarchFile);
            info.setNar(null, "noarch", getMavenProject().getGroupId()+":"+getMavenProject().getArtifactId()+":"+NAR_TYPE+":"+NAR_NO_ARCH);
        }
        
        // aol
        String bin = "bin";
        String lib = "lib";
        if (new File(narDirectory, bin).exists() || new File(narDirectory, lib).exists()) {
            // aol
            File archFile = new File(getOutputDirectory(), getFinalName()+"-"+getAOL()+"-"+type+"."+NAR_EXTENSION);
            nar(archFile, narDirectory, new String[] { bin, lib });
            addNarArtifact(NAR_TYPE, getAOL()+"-"+type, archFile);  
            info.setNar(null, type, getMavenProject().getGroupId()+":"+getMavenProject().getArtifactId()+":"+NAR_TYPE+":"+"${aol}-"+type);
        } 
        
        // FIXME hardcoded
        info.setBinding(null, type);
        
        try {
            info.writeToFile(propertiesFile);              
        } catch (IOException ioe) {
            throw new MojoExecutionException("Cannot write nar properties file", ioe);
        }
    }
    
    private void nar(File nar, File dir, String[] dirs) throws MojoExecutionException {
        try {
            if (nar.exists()) {
                nar.delete();
            }
            
            Archiver archiver = new ZipArchiver();
            // seems to return same archiver all the time
            // archiverManager.getArchiver(NAR_ROLE_HINT);
            for (int i=0; i<dirs.length; i++) {
                String[] includes = new String[] { dirs[i]+"/**" };
                archiver.addDirectory(dir, includes, null);
            }
            archiver.setDestFile(nar);
            archiver.createArchive();
        } catch (ArchiverException e) {
            throw new MojoExecutionException("Error while creating NAR archive.", e );
//        } catch (NoSuchArchiverException e) {
//            throw new MojoExecutionException("Error while creating NAR archive.", e );
        } catch (IOException e) {
            throw new MojoExecutionException("Error while creating NAR archive.", e );
        }    
    }
    
    private void addNarArtifact(String artifactType, String artifactClassifier, File artifactFile) {
        Artifact artifact = new AttachedNarArtifact( getMavenProject().getArtifact(), artifactType, artifactClassifier );
        
        artifact.setFile( artifactFile );
        artifact.setResolved( true );
  
// FIXME, the build number retrieved for SNAPSHOT is one too high (mvn 2.0)
// CHECK this may be due to the fact that multiple SNAPSHOTS are deployed and the build number (erroneously) incremented each time
        getMavenProject().addAttachedArtifact( artifact );
    }        
}
