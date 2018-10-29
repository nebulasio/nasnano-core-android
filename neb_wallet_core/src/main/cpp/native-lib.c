#include "native-lib.h"
#include "jni-util.h"
#include "libs/libsecp256k1/gp_secp256k1.h"
#include "libs/sha3.h"
#include "libs/ripemd.h"
#include "libs/base58.h"
#include "libs/libscrypt/libscrypt.h"
#include <stdint.h>
#include <stdlib.h>
#import <pthread.h>
/* Header for class io_nebulas_walletcore_Native */

pthread_mutex_t _mutex = PTHREAD_MUTEX_INITIALIZER;
#define LOCK pthread_mutex_lock(&_mutex)
#define UNLOCK pthread_mutex_unlock(&_mutex)

/*
 * Class:     io_nebulas_walletcore_Native
 * Method:    getPublicKeyFromPrivateKey
 * Signature: ([B)[B
 */
JNIEXPORT jbyteArray JNICALL Java_io_nebulas_walletcore_Native_getPublicKeyFromPrivateKey
        (JNIEnv *env, jobject obj, jbyteArray privateKey, jboolean compressed) {
    LOCK;
    ecc_start();
    uint8_t pubKey[100];
    size_t pubKeyLen = sizeof(pubKey) / sizeof(uint8_t);
    uint8_t *pk = bin_from_byte_array(env, privateKey, NULL);
    jbyteArray r = NULL;
    if(ecc_get_pubkey(pk, pubKey, &pubKeyLen, compressed)) {
        r = bin_to_byte_array(env, pubKey, pubKeyLen);
    }
    free(pk);
    ecc_stop();
    UNLOCK;
    return r;
}

/*
 * Class:     io_nebulas_walletcore_Native
 * Method:    verifyPrivateKey
 * Signature: ([B)Z
 */
JNIEXPORT jboolean JNICALL Java_io_nebulas_walletcore_Native_verifyPrivateKey
        (JNIEnv *env, jobject obj, jbyteArray privateKey) {
    LOCK;
    ecc_start();
    uint8_t *data = bin_from_byte_array(env, privateKey, NULL);
    bool r = ecc_verify_privatekey(data);
    free(data);
    ecc_stop();
    UNLOCK;
    return (jboolean) r;
}

/*
 * Class:     io_nebulas_walletcore_Native
 * Method:    verifyPublicKey
 * Signature: ([BZ)Z
 */
JNIEXPORT jboolean JNICALL Java_io_nebulas_walletcore_Native_verifyPublicKey
        (JNIEnv *env, jobject obj, jbyteArray publicKey, jboolean compressed) {
    LOCK;
    ecc_start();
    uint8_t *data = bin_from_byte_array(env, publicKey, NULL);
    bool r = ecc_verify_pubkey(data, compressed);
    free(data);
    ecc_stop();
    UNLOCK;
    return (jboolean) r;
}

/*
 * Class:     io_nebulas_walletcore_Native
 * Method:    sign
 * Signature: ([B[B[B)Z
 */
JNIEXPORT jboolean JNICALL Java_io_nebulas_walletcore_Native_sign
        (JNIEnv *env, jobject obj, jbyteArray hash, jbyteArray privateKey, jbyteArray outSignature) {
    LOCK;
    ecc_start();
    uint8_t *h = bin_from_byte_array(env, hash, NULL);
    uint8_t *pk = bin_from_byte_array(env, privateKey, NULL);
    uint8_t sig[65];
    int v;
    ecc_sign_recovery(pk, h, sig, &v);
    sig[64] = (uint8_t) v;
    free(h);
    free(pk);
    ecc_stop();
    bin_save_to_byte_array(env, outSignature, sig, 65);
    UNLOCK;
    return true;
}

/*
 * Class:     io_nebulas_walletcore_Native
 * Method:    verifySign
 * Signature: ([B[B[B)Z
 */
JNIEXPORT jboolean JNICALL Java_io_nebulas_walletcore_Native_verifySign
        (JNIEnv *env, jobject obj, jbyteArray hash32, jbyteArray signature65, jbyteArray outPubKey65) {
    LOCK;
    ecc_start();
    uint8_t *dHash = bin_from_byte_array(env, hash32, NULL);
    uint8_t *dSignature = bin_from_byte_array(env, signature65, NULL);
    int v = (int) dSignature[64];
    uint8_t pubKey[100];
    size_t pubKeyLen = 100;
    bool r = true;
    bool compressed;
    if (!ecc_verify_sig_recovery(dHash, dSignature, v, pubKey, &pubKeyLen, &compressed)) {
        r = false;
    } else {
        bin_save_to_byte_array(env, outPubKey65, pubKey, pubKeyLen);
    }
    free(dHash);
    free(dSignature);
    ecc_stop();
    UNLOCK;
    return (jboolean) r;
}

/*
 * Class:     io_nebulas_walletcore_Native
 * Method:    sha3256
 * Signature: ([B)[B
 */
JNIEXPORT jbyteArray JNICALL Java_io_nebulas_walletcore_Native_sha3256
        (JNIEnv *env, jobject obj, jobjectArray datas) {
    sha3_context c;
    sha3_Init256(&c);
    size_t count = (size_t)(*env)->GetArrayLength(env, datas);
    for (size_t i = 0; i < count; ++i) {
        jbyteArray data = (*env)->GetObjectArrayElement(env, datas, i);
        size_t len;
        uint8_t *d = bin_from_byte_array(env, data, &len);
        sha3_Update(&c, d, len);
        free(d);
    }
    uint8_t *hash = sha3_Finalize(&c);
    return bin_to_byte_array(env, hash, 32);
}

/*
 * Class:     io_nebulas_walletcore_Native
 * Method:    rmd160
 * Signature: ([B)[B
 */
JNIEXPORT jbyteArray JNICALL Java_io_nebulas_walletcore_Native_rmd160
        (JNIEnv *env, jobject obj, jbyteArray data) {
    size_t len;
    uint8_t *d = bin_from_byte_array(env, data, &len);
    size_t hashLen;
    uint8_t hash[1024];
    RMD(160, d, len, hash, &hashLen);
    free(d);
    return bin_to_byte_array(env, hash, hashLen);
}

/*
 * Class:     io_nebulas_walletcore_Native
 * Method:    scrypt
 * Signature: ([BLjava/lang/String;IIII)[B
 */
JNIEXPORT jbyteArray JNICALL Java_io_nebulas_walletcore_Native_scrypt
        (JNIEnv *env, jobject obj, jbyteArray salt, jbyteArray pwd, jint n, jint r, jint p, jint len) {
    size_t saltLen;
    uint8_t *saltData = bin_from_byte_array(env, salt, &saltLen);
    size_t pwdLen;
    uint8_t *pwdData = bin_from_byte_array(env, pwd, &pwdLen);
    uint8_t hashKeyData[len];
    libscrypt_scrypt(
            pwdData, pwdLen,
            saltData, saltLen,
            (uint64_t) n, (uint32_t) r, (uint32_t) p,
            hashKeyData, (size_t) len
    );
    free(saltData);
    free(pwdData);
    return bin_to_byte_array(env, hashKeyData, (size_t) len);
}

/*
 * Class:     io_nebulas_walletcore_Native
 * Method:    base58FromData
 * Signature: ([B)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_io_nebulas_walletcore_Native_base58FromData
        (JNIEnv *env, jobject obj, jbyteArray data) {
    size_t len;
    uint8_t *d = bin_from_byte_array(env, data, &len);
    char data58[1024];
    size_t data58Len = sizeof(data58) / sizeof(char);
    b58enc(data58, &data58Len, d, len); // data58Len 包含结尾 \0
    free(d);
    return to_jstring(env, data58);
}

/*
 * Class:     io_nebulas_walletcore_Native
 * Method:    base58ToData
 * Signature: (Ljava/lang/String;)[B
 */
JNIEXPORT jbyteArray JNICALL Java_io_nebulas_walletcore_Native_base58ToData
        (JNIEnv *env, jobject obj, jstring strBase58) {
    char *data58 = from_jstring(env, strBase58);
    uint8_t bin[1024];
    size_t binLen = sizeof(bin) / sizeof(uint8_t);
    jbyteArray r = NULL;
    if(b58tobin(bin, &binLen, data58, strlen(data58))) {
        r = bin_to_byte_array(env, bin + 1024 - binLen, binLen);
    }
    free(data58);
    return r;
}


