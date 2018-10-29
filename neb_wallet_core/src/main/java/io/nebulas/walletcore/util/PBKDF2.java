package io.nebulas.walletcore.util;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class PBKDF2 {

    public static final int PBKDF2AlgHmacSHA256 = 0;
    public static final int PBKDF2AlgHmacSHA384 = 1;
    public static final int PBKDF2AlgHmacSHA512 = 2;

    public static byte[] shaHMAC(int alg, byte[] key, byte[] pwd) {
        if (key.length == 0) {
            key = new byte[] { 0x00 };
        }
        try {
            String algName = algNameWithAlg(alg);
            Mac mac = Mac.getInstance(algName);
            mac.init(new SecretKeySpec(key, algName));
            return mac.doFinal(pwd);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return pwd;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            return pwd;
        }
    }

    /**
     * Pass "SHA256" or "SHA384" or "SHA512" as the parameter alg.
     *
     * @param alg
     * @param pwd
     * @param salt
     * @param iterations
     * @return
     */
    public static byte[] hmac(int alg, byte[] pwd, byte[] salt, int iterations) {
        int dkLen = 64;
        int hLen = 64;
        switch (alg) {
            case PBKDF2AlgHmacSHA256:
                dkLen = 32;
                hLen = 32;
                break;
            case PBKDF2AlgHmacSHA384:
                dkLen = 48;
                hLen = 48;
                break;
            case PBKDF2AlgHmacSHA512:
                dkLen = 64;
                hLen = 64;
                break;
        }
        int l = (int) Math.ceil(dkLen / hLen);
        int r = dkLen - (l - 1) * hLen;
        byte[] dk = new byte[dkLen];
        for (int i = 1; i <= l; i++) {
            byte[] T = F(alg, pwd, salt, iterations, i);
            for (int k = 0; k < T.length; k++) {
                if (i-1+k < dk.length) {
                    dk[i-1+k] = T[k];
                }
            }
        }
        return dk;
    }

    private static byte[] F (int alg, byte[] pwd, byte[] salt, int iterations, int i) {
        byte[] Si = new byte[salt.length+4];
        System.arraycopy(salt, 0, Si, 0, salt.length);
        byte[] iByteArray = ByteBuffer.allocate(4).putInt(i).array();
        System.arraycopy(iByteArray, 0, Si, salt.length, iByteArray.length);
        byte[] U = shaHMAC(alg, pwd, Si);
        byte[] T = new byte[U.length];
        System.arraycopy(U, 0, T, 0, T.length);
        for (int c = 1; c < iterations; c++) {
            U = shaHMAC(alg, pwd, U);
            for (int k = 0; k < U.length; k++) {
                T[k] = (byte) (((int) T[k]) ^ ((int) U[k]));
            }
        }
        return T;
    }

    private static String algNameWithAlg(int alg) {
        String strAlg = "SHA512";
        switch (alg) {
            case PBKDF2AlgHmacSHA256:
                strAlg = "SHA256";
                break;
            case PBKDF2AlgHmacSHA384:
                strAlg = "SHA384";
                break;
            case PBKDF2AlgHmacSHA512:
                strAlg = "SHA512";
                break;
        }
        return "Hmac" + strAlg;
    }
}
