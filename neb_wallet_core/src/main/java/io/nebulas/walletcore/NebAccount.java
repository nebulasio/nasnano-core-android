package io.nebulas.walletcore;

import io.nebulas.walletcore.exceptions.IllegalKeystoreException;
import io.nebulas.walletcore.exceptions.IllegalPrivateKeyException;
import io.nebulas.walletcore.exceptions.NebulasException;
import io.nebulas.walletcore.exceptions.WrongPasswordException;
import io.nebulas.walletcore.transaction.NebTransaction;
import io.nebulas.walletcore.util.BCUtil;
import io.nebulas.walletcore.util.Callback;

public class NebAccount {

    private final byte ADDRESS_PREFIX = 25;
    private final byte ADDRESS_NORMAL_TYPE = 87;

    private byte[] privateKey;
    private byte[] compressedPublicKey;
    private byte[] uncompressedPublicKey;

    public NebAccount() {
        this.privateKey = BCUtil.randomBytes(32);
    }

    public NebAccount(String privateKey) throws NebulasException {
        this.privateKey = BCUtil.bytesFromHex(privateKey);
        if (this.privateKey.length != 32) {
            throw new IllegalPrivateKeyException();
        }
    }

    public NebAccount(String keystore, String pwd) throws NebulasException {
        NebKeystore k = null;
        try {
            k = NebKeystore.fromJson(keystore);
        } catch (Exception e) {
            throw new IllegalKeystoreException();
        }
        if (k == null || !k.check()) {
            throw new IllegalKeystoreException();
        }
        this.privateKey = k.getPrivateKeyWithPwd(pwd);
        if (this.privateKey == null) {
            throw new WrongPasswordException();
        }
        if (this.privateKey.length > 32) {
            this.privateKey = BCUtil.subBytes(this.privateKey, 0, 32);
        }
    }

    private byte[] getCompressedPublicKey() {
        if (compressedPublicKey == null) {
            compressedPublicKey = Native.getPublicKeyFromPrivateKey(this.privateKey, true);
        }
        return compressedPublicKey;
    }

    private byte[] getUncompressedPublicKey() {
        if (uncompressedPublicKey == null) {
            uncompressedPublicKey = Native.getPublicKeyFromPrivateKey(this.privateKey, false);
        }
        return uncompressedPublicKey;
    }

    private Secp256k1Signature sign(byte[] hash32) {
        byte[] sigData = new byte[65];
        if (!Native.sign(hash32, this.privateKey, sigData)) {
            return null;
        }
        return new Secp256k1Signature(sigData);
    }

    private boolean verifySign(byte[] hash, byte[] signature, String address, Callback<String, byte[]> addressCreator) {
        byte[] pubKey = new byte[65];
        if (!Native.verifySign(hash, signature, pubKey)) {
            return false;
        }
        if (addressCreator == null) {
            return false;
        }
        String a = addressCreator.call(pubKey);
        return a.equals(address);
    }

    public String getPrivateKey() {
        return BCUtil.bytesToHex(privateKey);
    }

    public String getAddress() {
        byte[] pubKey = getUncompressedPublicKey();
        byte[] content = Native.sha3256(pubKey);
        content = Native.rmd160(content);
        content = BCUtil.bytesConcat(new byte[]{ADDRESS_NORMAL_TYPE}, content);
        content = BCUtil.bytesConcat(new byte[]{ADDRESS_PREFIX}, content);
        byte[] checkSum = BCUtil.subBytes(Native.sha3256(content), 0, 4);
        content = BCUtil.bytesConcat(content, checkSum);
        return Native.base58FromData(content);
    }

    public String createNewKeystore(String pwd) {
        NebKeystore k = NebKeystore.fromPrivateKeyAndPwd(privateKey, pwd);
        k.address = getAddress();
        return k.getJson();
    }

    public String signTransaction(NebTransaction transaction) {
        NebTransactionData data = new NebTransactionData(transaction);
        data.sign = this.sign(data.getTxHash()).getSignature();
        return data.encode();
    }
}
