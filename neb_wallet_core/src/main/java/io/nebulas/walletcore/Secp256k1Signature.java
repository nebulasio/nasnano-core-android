package io.nebulas.walletcore;

class Secp256k1Signature {

    private byte[] signature;

    public Secp256k1Signature(byte[] signature) {
        this.signature = signature;
    }

    public byte[] getSignature() {
        return signature;
    }

    public byte[] getR() {
        byte[] r = new byte[32];
        System.arraycopy(signature, 0, r, 0, 32);
        return r;
    }

    public byte[] getS() {
        byte[] s = new byte[32];
        System.arraycopy(signature, 32, s, 0, 32);
        return s;
    }

    public int getV() {
        return (int)signature[64];
    }
}
