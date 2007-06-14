public class HelloWorldJNI {
    static {
        System.loadLibrary("helloworldjni-1.0-SNAPSHOT");
    }

    public native String sayHello();

    public static void main( String[] args ) {
        HelloWorldJNI app = new HelloWorldJNI();
        System.out.println( app.sayHello() );
    }
}

