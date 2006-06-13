// Copyright FreeHEP, 2005.
package org.freehep.maven.nar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

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
 * @version $Id: src/main/java/org/freehep/maven/nar/NarPackageMojo.java 83229295dbc0 2006/06/13 18:24:24 duns $
 */
public class NarPackageMojo extends AbstractNarMojo {
            
    public void execute() throws MojoExecutionException, MojoFailureException {
        String nars = null;
                      
        // noarch
        String include = "nar/include";
        if (new File(getOutputDirectory(), include).exists()) {   
            File noarchFile = new File(getOutputDirectory(), getFinalName()+"-"+NAR_NO_ARCH+"."+NAR_EXTENSION);
            nar(noarchFile, getOutputDirectory(), new String[] {include});
            addNarArtifact(NAR_TYPE, NAR_NO_ARCH, noarchFile);
            nars = (nars == null) ? "" : nars + ",";
            nars += mavenProject.getGroupId()+":"+mavenProject.getArtifactId()+":"+NAR_TYPE+":"+NAR_NO_ARCH+":"+mavenProject.getVersion();            
        }
        
        // arch
        String bin = "nar/bin";
        String lib = "nar/lib";
        if (new File(getOutputDirectory(), bin).exists() || new File(getOutputDirectory(), lib).exists()) {
            // arch
            File archFile = new File(getOutputDirectory(), getFinalName()+"-"+getAOL()+"."+NAR_EXTENSION);
            nar(archFile, getOutputDirectory(), new String[] {bin, lib});
            addNarArtifact(NAR_TYPE, getAOL(), archFile);        
            nars = (nars == null) ? "" : nars + ",";
            nars += mavenProject.getGroupId()+":"+mavenProject.getArtifactId()+":"+NAR_TYPE+":"+"${aol}"+":"+mavenProject.getVersion();
        } 
        
        // General properties.nar file
        File propertiesDir = new File(getOutputDirectory(), "classes/META-INF/nar/"+mavenProject.getGroupId()+"/"+mavenProject.getArtifactId());
        if (!propertiesDir.exists()) {
            propertiesDir.mkdirs();
        }
        File propertiesFile = new File(propertiesDir, NAR_PROPERTIES);
        try {
            Properties properties = new Properties();
            if (propertiesFile.exists()) {
                properties.load(new FileInputStream(propertiesFile));
            }

            properties.setProperty("nars", properties.getProperty("nars", nars));
            
            FileOutputStream out = new FileOutputStream(propertiesFile);
            properties.store(out, "FreeHEP-NAR-Plugin generated properties file");
            out.close();
        } catch (IOException e) {
            throw new MojoExecutionException("Error while creating '"+propertiesFile.getPath()+"' file.", e );
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
        Artifact artifact = new AttachedNarArtifact( mavenProject.getArtifact(), artifactType, artifactClassifier );
        
        artifact.setFile( artifactFile );
        artifact.setResolved( true );
  
// FIXME, the build number retrieved for SNAPSHOT is one too high (mvn 2.0)
// CHECK
        mavenProject.addAttachedArtifact( artifact );
    }        
}
