//
// Created by 郭平 on 2018/4/16.
//

#include <jni.h>
#include <stdint.h>

#ifndef GPBCLIB_JNI_UTIL_H
#define GPBCLIB_JNI_UTIL_H

#import <stdbool.h>
#import <stdlib.h>

jbyteArray bin_to_byte_array(JNIEnv *env, const uint8_t *buf, size_t len);

void bin_save_to_byte_array(JNIEnv *env, jbyteArray array, const uint8_t *buf, size_t len);

uint8_t * bin_from_byte_array(JNIEnv *env, jbyteArray array, size_t *outLen);

char* from_jstring(JNIEnv *env, jstring jstr);

jstring to_jstring(JNIEnv *env, const char* pat);

#endif //GPBCLIB_JNI_UTIL_H
