package io.nebulas.walletcore.transaction;

public class NebBinaryData {
    public NebBinaryData(String data) {
        Data = data.getBytes();
    }
    public byte[] Data;
}
