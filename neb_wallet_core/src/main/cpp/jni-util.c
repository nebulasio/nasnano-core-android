//
// Created by 郭平 on 2018/4/16.
//

#include <stdlib.h>
#include <string.h>
#include "jni-util.h"

jbyteArray bin_to_byte_array(JNIEnv *env, const uint8_t *buf, size_t len) {
    jbyteArray array = (*env)->NewByteArray(env, (jsize)len);
    (*env)->SetByteArrayRegion(env, array, 0, (jsize)len, (jbyte *)buf);
    return array;
}

void bin_save_to_byte_array(JNIEnv *env, jbyteArray array, const uint8_t *buf, size_t len) {
    size_t len1 = (size_t)(*env)->GetArrayLength(env, array);
    len = len < len1 ? len : len1;
    (*env)->SetByteArrayRegion(env, array, 0, (jsize)len, (jbyte *)buf);
}

uint8_t * bin_from_byte_array(JNIEnv *env, jbyteArray array, size_t *outLen) {
    size_t len = (size_t)(*env)->GetArrayLength(env, array);
    uint8_t *buf = (uint8_t *) malloc(len);
    (*env)->GetByteArrayRegion(env, array, 0, (jsize)len, (jbyte *)buf);
    if (outLen) {
        *outLen = len;
    }
    return buf;
}

char* from_jstring(JNIEnv *env, jstring jstr) {
    char* pStr = NULL;
    jclass     jstrObj   = (*env)->FindClass(env, "java/lang/String");
    jstring    encode    = (*env)->NewStringUTF(env, "utf-8");
    jmethodID  methodId  = (*env)->GetMethodID(env, jstrObj, "getBytes", "(Ljava/lang/String;)[B");
    jbyteArray byteArray = (jbyteArray)(*env)->CallObjectMethod(env, jstr, methodId, encode);
    jsize      strLen    = (*env)->GetArrayLength(env, byteArray);
    jbyte      *jBuf     = (*env)->GetByteArrayElements(env, byteArray, JNI_FALSE);
    if (jBuf > 0) {
        pStr = (char*)malloc(strLen + 1);
        if (!pStr) {
            return NULL;
        }
        memcpy(pStr, jBuf, strLen);
        pStr[strLen] = 0;
    }
    (*env)->ReleaseByteArrayElements(env, byteArray, jBuf, 0);
    return pStr;
}

jstring to_jstring(JNIEnv *env, const char* cstr) {
    jclass strClass = (*env)->FindClass(env, "java/lang/String");
    jmethodID ctorID = (*env)->GetMethodID(env, strClass, "<init>", "([BLjava/lang/String;)V");
    jstring encoding = (*env)->NewStringUTF(env, "utf-8");
    jbyteArray bytes = (*env)->NewByteArray(env, strlen(cstr));
    (*env)->SetByteArrayRegion(env, bytes, 0, strlen(cstr), (jbyte*)cstr);
    return (jstring)(*env)->NewObject(env, strClass, ctorID, bytes, encoding);
}