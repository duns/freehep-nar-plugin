
#include <iostream>
#include <cstdlib>

#include <jni.h>
#include "org_freehep_maven_nar_test_HelloWorld.h"

JNIEXPORT void JNICALL 
Java_org_freehep_maven_nar_test_HelloWorld_hello(JNIEnv *env, jobject obj) {
    std::cout << "Hello from the NAR Plugin" << std::endl;
}

