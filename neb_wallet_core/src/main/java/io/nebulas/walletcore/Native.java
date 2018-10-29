package io.nebulas.walletcore;

class Native {

    static {
        System.loadLibrary("Secp256k1");
        System.loadLibrary("NebWalletCore");
    }

    private Native() {
    }

    public static native byte[] getPublicKeyFromPrivateKey(byte[] privateKey, boolean compressed);

    public static native boolean verifyPrivateKey(byte[] privateKey);

    public static native boolean verifyPublicKey(byte[] publicKey, boolean compressed);

    public static native boolean sign(byte[] hash, byte[] privateKey, byte[] outSignature65);

    public static native boolean verifySign(byte[] hash, byte[] signature65, byte[] outPubKey65);

    public static native byte[] sha3256(byte[][] datas);

    public static native byte[] rmd160(byte[] data);

    public static native byte[] scrypt(byte[] salt, byte[] pwd, int n, int r, int p, int len);

    public static native String base58FromData(byte[] data);

    public static native byte[] base58ToData(String strBase58);

    public static byte[] sha3256(byte[] data) {
        return sha3256(new byte[][]{data});
    }

}
