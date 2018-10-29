package io.nebulas.walletcore.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class BCUtil {

    private final static char[] hexArray = "0123456789abcdef".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] bytesFromHex(String hex) {
        int len = hex.length();
        byte[] data = new byte[len % 2 == 0 ? (len / 2) : (len / 2 + 1)];
        for (int i = len - 1; i >= 0; i -= 2) {
            int j = i - 1;
            if (j >= 0) {
                data[i / 2] = (byte) ((Character.digit(hex.charAt(j), 16) << 4) + Character.digit(hex.charAt(i), 16));
            } else {
                data[i / 2] = (byte) Character.digit(hex.charAt(i), 16);
            }
        }
        return data;
    }

    public static byte[] bytesFromDecimal(BigDecimal decimal) {
        return bytesFromHex(decimal2Hex(decimal));
    }

    public static BigDecimal hex2Decimal(String s) {
        String digits = "0123456789ABCDEF";
        s = s.toUpperCase();
        BigDecimal val = new BigDecimal(0);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int d = digits.indexOf(c);
            val = val.multiply(new BigDecimal(16)).add(new BigDecimal(d));
        }
        return val;
    }

    public static String decimal2Hex(BigDecimal d) {
        String digits = "0123456789ABCDEF";
        BigDecimal d0 = new BigDecimal(0);
        if (d.compareTo(d0) <= 0) {
            return "0";
        }
        String hex = "";
        BigDecimal d16 = new BigDecimal(16);
        while (d.compareTo(d0) > 0) {
            BigDecimal digit = d.divideAndRemainder(d16)[1].setScale(0, RoundingMode.FLOOR);
            hex = digits.charAt(Integer.parseInt(digit.toString())) + hex;
            d = d.divide(d16, RoundingMode.FLOOR);
        }
        return hex;
    }

    public static byte[] randomBytes(int len) {
        byte[] bytes = new byte[len];
        new Random(System.currentTimeMillis()).nextBytes(bytes);
        return bytes;
    }

    public static byte[] bytesConcat(byte[] bytes1, byte[] bytes2) {
        byte[] r = new byte[bytes1.length + bytes2.length];
        System.arraycopy(bytes1, 0, r, 0, bytes1.length);
        System.arraycopy(bytes2, 0, r, bytes1.length, bytes2.length);
        return r;
    }

    public static byte[] subBytes(byte[] bytes, int start, int len) {
        byte[] r = new byte[len];
        System.arraycopy(bytes, start, r, 0, len);
        return r;
    }

    public static boolean equals(byte[] data, byte[] data1) {
        if (data == null && data1 == null) {
            return true;
        }
        if (data == null || data1 == null) {
            return false;
        }
        if (data.length != data1.length) {
            return false;
        }
        for (int i = 0; i < data.length; ++i) {
            if (data[i] != data1[i]) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEmpty(byte[] data) {
        return data == null || data.length == 0;
    }

    public static boolean checkBase58(String base58) {
        return base58.matches("^[1-9A-HJ-NP-Za-km-z]+$");
    }
}
