package io.nebulas.walletcore.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {

    public static byte[] encrypt128Ctl(byte[] data, byte[] ivData, byte[] keyData) {
        try {
            IvParameterSpec iv = new IvParameterSpec(ivData);
            SecretKeySpec skeySpec = new SecretKeySpec(keyData, "AES");
            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            return cipher.doFinal(data);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static byte[] decrypt128Ctl(byte[] data, byte[] ivData, byte[] keyData) {
        try {
            IvParameterSpec iv = new IvParameterSpec(ivData);
            SecretKeySpec skeySpec = new SecretKeySpec(keyData, "AES");
            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            return cipher.doFinal(data);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
