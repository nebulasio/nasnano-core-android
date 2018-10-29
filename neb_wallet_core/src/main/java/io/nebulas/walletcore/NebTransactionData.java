package io.nebulas.walletcore;

import android.text.TextUtils;

import java.lang.reflect.Array;
import java.math.BigDecimal;

import io.nebulas.walletcore.transaction.NebBinaryData;
import io.nebulas.walletcore.transaction.NebCallData;
import io.nebulas.walletcore.transaction.NebDeployData;
import io.nebulas.walletcore.transaction.NebTransaction;
import io.nebulas.walletcore.util.BCUtil;
import io.nebulas.walletcore.util.JsonUtil;

class NebTransactionData {

    public final static int ALG_SECP256K1 = 1;

    public final static String DATA_TYPE_BINARY = "binary";
    public final static String DATA_TYPE_DEPLOY = "deploy";
    public final static String DATA_TYPE_CALL = "call";

    static class NebData {
        public NebData(Object data) {
            if (data == null) {
                return;
            }
            if (data.getClass() == NebBinaryData.class) {
                type = DATA_TYPE_BINARY;
            } else if (data.getClass() == NebDeployData.class) {
                type = DATA_TYPE_DEPLOY;
            } else if (data.getClass() == NebCallData.class) {
                type = DATA_TYPE_CALL;
            }
            payload = TextUtils.htmlEncode(JsonUtil.serialize(data)).getBytes();
        }
        String type = DATA_TYPE_BINARY;
        byte[] payload = null;
    }

    public byte[] hash;
    public byte[] from;
    public byte[] to;
    public byte[] value;
    public long nonce;
    public long timestamp;
    public byte[] data;
    public int chainID;
    public byte[] gasPrice;
    public byte[] gasLimit;
    public int alg;
    public byte[] sign;

    public NebTransactionData() {
    }

    public NebTransactionData(NebTransaction tx) {
        from = Native.base58ToData(tx.from);
        to = Native.base58ToData(tx.to);
        value = BCUtil.bytesFromDecimal(tx.value);
        nonce = tx.nonce;
        timestamp = System.currentTimeMillis();
        data = bytesFromData(new NebData(tx.data));
        chainID = tx.chainID;
        gasPrice = BCUtil.bytesFromDecimal(tx.gasPrice);
        gasLimit = BCUtil.bytesFromDecimal(tx.gasLimit);
        alg = ALG_SECP256K1;

        hash = getTxHash();
    }

    byte[] getTxHash() {
        byte[][] datas = new byte[][] {
                from,
                to,
                padData(value, 16),
                padData(BCUtil.bytesFromDecimal(new BigDecimal(nonce)), 8),
                padData(BCUtil.bytesFromDecimal(new BigDecimal(timestamp)), 8),
                data,
                padData(BCUtil.bytesFromDecimal(new BigDecimal(chainID)), 4),
                padData(gasPrice, 16),
                padData(gasLimit, 16)

        };
        return Native.sha3256(datas);
    }

    String encode() {
        byte[] data = null; // TODO: 2018/10/29 转化为protobuf格式数据
        String result = null; // TODO: 将data用base64编码
        return result;
    }

    private byte[] padData(byte[] data, int len) {
        byte[] result = new byte[len];
        int min = Math.min(data.length, len);
        System.arraycopy(data, data.length - min, result, result.length - min, min);
        return result;
    }

    private byte[] bytesFromData(NebData data) {
        // TODO: 2018/10/29 将data转化为protobuf格式数据 并返回
        return null;
    }

}
