// Copyright 2005, FreeHEP.
package org.freehep.maven.nar;
 
import java.util.regex.Pattern;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;

/**
 * @author Mark Donszelmann
 * @version $Id: src/main/java/org/freehep/maven/nar/NarUtil.java 5e680f13e167 2006/06/21 21:17:37 duns $
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
    
    /**
     * Replaces target with replacement in string. For jdk 1.4 compatiblity.
     * @param target
     * @param replacement
     * @param string
     * @return
     */
    public static String replace(CharSequence target, CharSequence replacement, String string) {
        return Pattern.compile(quote(target.toString())/*, Pattern.LITERAL jdk 1.4 */).matcher(
            string).replaceAll(/* Matcher. jdk 1.4 */ quoteReplacement(replacement.toString()));
    }       

    /* for jdk 1.4 */
    private static String quote(String s) {
        int slashEIndex = s.indexOf("\\E");
        if (slashEIndex == -1)
            return "\\Q" + s + "\\E";

        StringBuffer sb = new StringBuffer(s.length() * 2);
        sb.append("\\Q");
        slashEIndex = 0;
        int current = 0;
        while ((slashEIndex = s.indexOf("\\E", current)) != -1) {
            sb.append(s.substring(current, slashEIndex));
            current = slashEIndex + 2;
            sb.append("\\E\\\\E\\Q");
        }
        sb.append(s.substring(current, s.length()));
        sb.append("\\E");
        return sb.toString();
    }

    /* for jdk 1.4 */
    private static String quoteReplacement(String s) {
        if ((s.indexOf('\\') == -1) && (s.indexOf('$') == -1))
            return s;
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\') {
                sb.append('\\'); sb.append('\\');
            } else if (c == '$') {
                sb.append('\\'); sb.append('$');
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
