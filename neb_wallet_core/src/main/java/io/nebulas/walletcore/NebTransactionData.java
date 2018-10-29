package io.nebulas.walletcore;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.google.protobuf.ByteString;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Arrays;

import io.nebulas.ProtoTransaction;
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
    public NebData data;
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
//        timestamp = System.currentTimeMillis();
        timestamp = 1540797009L;
        data = new NebData(tx.data);
        chainID = tx.chainID;
        gasPrice = BCUtil.bytesFromDecimal(tx.gasPrice);
        gasLimit = BCUtil.bytesFromDecimal(tx.gasLimit);
        alg = ALG_SECP256K1;

        hash = getTxHash();
    }

    byte[] getTxHash() {
        byte[][] datas = new byte[][]{
                from,
                to,
                padData(value, 16),
                padData(BCUtil.bytesFromDecimal(new BigDecimal(nonce)), 8),
                padData(BCUtil.bytesFromDecimal(new BigDecimal(timestamp)), 8),
                getProtoData(data),
                padData(BCUtil.bytesFromDecimal(new BigDecimal(chainID)), 4),
                padData(gasPrice, 16),
                padData(gasLimit, 16)

        };
        for (byte[] bs: datas) {
            Log.d("Neb***", Arrays.toString(bs));
        }
        return Native.sha3256(datas);
    }

    String encode() {
        ProtoTransaction.Data.Builder dataBuilder = ProtoTransaction.Data.newBuilder();
        dataBuilder.setType(data.type);
        if (data.payload!=null) {
            dataBuilder.setPayload(ByteString.copyFrom(data.payload));
        }

        ProtoTransaction.Transaction.Builder builder = ProtoTransaction.Transaction.newBuilder();
        builder.setChainId(this.chainID);
        builder.setHash(ByteString.copyFrom(hash))
                .setFrom(ByteString.copyFrom(from))
                .setTo(ByteString.copyFrom(to))
                .setValue(ByteString.copyFrom(value))
                .setNonce(nonce)
                .setTimestamp(timestamp)
                .setGasPrice(ByteString.copyFrom(gasPrice))
                .setGasLimit(ByteString.copyFrom(gasLimit))
                .setAlg(alg)
                .setSign(ByteString.copyFrom(sign))
                .setData(dataBuilder.build());


        byte[] data = builder.build().toByteArray(); // TODO: 2018/10/29 转化为protobuf格式数据
        String result = Base64.encodeToString(data, 0); // TODO: 将data用base64编码
        return result;
    }

    private byte[] padData(byte[] data, int len) {
        byte[] result = new byte[len];
        int min = Math.min(data.length, len);
        System.arraycopy(data, data.length - min, result, result.length - min, min);
        return result;
    }

    public byte[] getProtoData(NebData data) {
        ProtoTransaction.Data.Builder dataBuilder = ProtoTransaction.Data.newBuilder();
        dataBuilder.setType(data.type);
        if (data.payload != null) {
            dataBuilder.setPayload(ByteString.copyFrom(data.payload));
        }
        return dataBuilder.build().toByteArray();
    }

}
