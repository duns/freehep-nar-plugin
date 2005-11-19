// Copyright 2005, FreeHEP.
package org.freehep.maven.nar;
 
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;

/**
 * @author Mark Donszelmann
 * @version $Id: src/main/java/org/freehep/maven/nar/NarUtil.java bcdae088c368 2005/11/19 07:52:18 duns $
 */
public class NarUtil {
    
    /**
     * Returns the Bcel Class corresponding to the given class filename
     *
     * @param filename the absolute file name of the class
     * @return the Bcel Class.
     */
    public static final JavaClass getBcelClass(String filename) {
        try {
            ClassParser parser = new ClassParser(filename);
            return parser.parse();            
        } catch (Exception e) {
            System.err.println("\nError parsing " + filename + ": " + e + "\n");
            return null;
        }
    }

    /**
     * Returns the header file name (javah) corresponding to the given class file name
     *
     * @param filename the absolute file name of the class
     * @return the header file name.
     */
    public static final String getHeaderName(String base, String filename) {
        try {
            base = base.replaceAll("\\\\","/");
            filename = filename.replaceAll("\\\\","/");
            if (!filename.startsWith(base)) {
                System.out.println("\nError "+filename+" does not start with "+base);
                return null;
            }
            String header = filename.substring(base.length()+1);
            header = header.replaceAll("/","_");
            header = header.replaceAll("\\.class",".h");
            return header;
        } catch (Exception e) {
            System.err.println("\nError parsing " + filename + ": " + e + "\n");
            return null;
        }
    }
}
