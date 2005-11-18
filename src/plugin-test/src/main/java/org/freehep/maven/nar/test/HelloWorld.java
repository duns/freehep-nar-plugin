package org.freehep.maven.nar.test;

public class HelloWorld {
    public native void hello();

    public static void main(String args[]) {
        System.out.println("Loading Native Library");
        HelloWorld say = new HelloWorld();
        System.loadLibrary("helloworld");
        say.hello();
        System.out.println("done");
    }
}

