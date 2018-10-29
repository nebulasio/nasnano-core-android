package io.nebulas.walletcore;

import android.text.TextUtils;

import io.nebulas.walletcore.util.AES;
import io.nebulas.walletcore.util.PBKDF2;
import io.nebulas.walletcore.util.BCUtil;
import io.nebulas.walletcore.util.JsonUtil;

import java.util.UUID;

class NebKeystore {

    private static final int VERSION3 = 3;
    private static final int VERSION_CURRENT = 4;

    static class NebKeystoreCipherParams {
        String iv;
    }

    static class NebKeystoreKdfParams {
        int dklen;
        String salt;
        int n;
        int r;
        int p;
        int c;
        String prf;
    }

    static class NebKeystoreCrypto {
        String cipher;
        String ciphertext;
        NebKeystoreCipherParams cipherparams;
        String kdf;
        NebKeystoreKdfParams kdfparams;
        String mac;
        String machash = "sha3256";

        boolean isScrypt() {
            return kdf.toLowerCase().equals("scrypt");
        }
    }

    int version;
    String id;
    String address;
    NebKeystoreCrypto crypto;

    static NebKeystore fromJson(String json) {
        // TODO: 2018/10/28 用 JSONObject 替换 Gson 可以减少sdk依赖
        return JsonUtil.deserialize(json, NebKeystore.class);
    }

    static NebKeystore fromPrivateKeyAndPwd(byte[] privateKey, String pwd) {
        NebKeystore keystore = new NebKeystore();
        keystore.id = UUID.randomUUID().toString();
        keystore.version = VERSION_CURRENT;

        NebKeystoreCrypto crypto = new NebKeystoreCrypto();

        NebKeystoreKdfParams kdfparams = new NebKeystoreKdfParams();
        kdfparams.salt = BCUtil.bytesToHex(BCUtil.randomBytes(32));
        kdfparams.dklen = 32;

        crypto.kdf = "script";
        kdfparams.n = 4096;
        kdfparams.r = 8;
        kdfparams.p = 1;

        crypto.kdfparams = kdfparams;

        crypto.cipher = "aes-128-ctr";
        NebKeystoreCipherParams cparams = new NebKeystoreCipherParams();
        cparams.iv = BCUtil.bytesToHex(BCUtil.randomBytes(16));
        crypto.cipherparams = cparams;

        keystore.crypto = crypto;

        byte[] derivedKey = keystore.getDerivedKeyWithPwd(pwd);
        byte[] ivData = BCUtil.bytesFromHex(keystore.crypto.cipherparams.iv);
        byte[] keyData = BCUtil.subBytes(derivedKey, 0, 16);
        byte[] ciphertextData = AES.encrypt128Ctl(privateKey, ivData, keyData);
        crypto.ciphertext = BCUtil.bytesToHex(ciphertextData);
        byte[] data = BCUtil.subBytes(derivedKey, 16, 16);
        data = BCUtil.bytesConcat(data, ciphertextData);
        data = BCUtil.bytesConcat(data, BCUtil.bytesFromHex(cparams.iv));
        data = BCUtil.bytesConcat(data, crypto.cipher.getBytes());
        byte[] mac = Native.sha3256(data);
        crypto.mac = BCUtil.bytesToHex(mac);

        return keystore;
    }

    String getJson() {
        // TODO: 2018/10/28 用 JSONObject 替换 Gson 可以减少sdk依赖
        return JsonUtil.serialize(this);
    }

    byte[] getPrivateKeyWithPwd(String pwd) {
        if (!check()) {
            return null;
        }
        byte[] derivedKey = getDerivedKeyWithPwd(pwd);
        byte[] data = BCUtil.subBytes(derivedKey, 16, 16);
        byte[] ciphertextData = BCUtil.bytesFromHex(this.crypto.ciphertext);
        data = BCUtil.bytesConcat(data, ciphertextData);
        if (version == VERSION_CURRENT) {
            data = BCUtil.bytesConcat(data, BCUtil.bytesFromHex(this.crypto.cipherparams.iv));
            data = BCUtil.bytesConcat(data, crypto.cipher.getBytes());
        }
        String m = BCUtil.bytesToHex(Native.sha3256(data));
        if (!this.crypto.mac.toLowerCase().equals(m)) {
            return null;
        }

        byte[] ivData = BCUtil.bytesFromHex(this.crypto.cipherparams.iv);
        byte[] keyData = BCUtil.subBytes(derivedKey,0, 16);
        byte[] r = AES.decrypt128Ctl(ciphertextData, ivData, keyData);
        if (r.length < 32) {
            r = BCUtil.bytesConcat(BCUtil.bytesFromHex("00"), r);
        }
        return r;
    }

    boolean check() {
        return !(this.crypto == null ||
                TextUtils.isEmpty(this.crypto.kdf) ||
                this.crypto.kdfparams == null ||
                (this.crypto.isScrypt() && !TextUtils.isEmpty(this.crypto.kdfparams.prf) && !this.crypto.kdfparams.prf.equals("hmac-sha256")) ||
                TextUtils.isEmpty(this.crypto.mac) ||
                TextUtils.isEmpty(this.crypto.cipher) ||
                TextUtils.isEmpty(this.crypto.ciphertext) ||
                this.crypto.cipherparams == null);
    }

    private byte[] getDerivedKeyWithPwd(String pwd) {
        byte[] saltData = BCUtil.bytesFromHex(this.crypto.kdfparams.salt);
        byte[] pwdData = pwd.getBytes();
        if (this.crypto.isScrypt()) {
            return Native.scrypt(saltData, pwdData, this.crypto.kdfparams.n, this.crypto.kdfparams.r, this.crypto.kdfparams.p, this.crypto.kdfparams.dklen);
        } else {
            return PBKDF2.hmac(PBKDF2.PBKDF2AlgHmacSHA256, pwdData, saltData, this.crypto.kdfparams.c);
        }
    }

}
