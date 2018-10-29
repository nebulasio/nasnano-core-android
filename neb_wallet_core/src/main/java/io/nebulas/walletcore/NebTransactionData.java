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

    final static int ALG_SECP256K1 = 1;

    final static String DATA_TYPE_BINARY = "binary";
    final static String DATA_TYPE_DEPLOY = "deploy";
    final static String DATA_TYPE_CALL = "call";

    static class NebData {
        NebData(Object data) {
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

    byte[] hash;
    byte[] from;
    byte[] to;
    byte[] value;
    long nonce;
    long timestamp;
    NebData data;
    int chainID;
    byte[] gasPrice;
    byte[] gasLimit;
    int alg;
    byte[] sign;

    NebTransactionData(NebTransaction tx) {
        from = Native.base58ToData(tx.from);
        to = Native.base58ToData(tx.to);
        value = padData(BCUtil.bytesFromDecimal(tx.value), 16);
        nonce = tx.nonce;
        timestamp = System.currentTimeMillis();
        data = new NebData(tx.data);
        chainID = tx.chainID;
        gasPrice = padData(BCUtil.bytesFromDecimal(tx.gasPrice), 16);
        gasLimit = padData(BCUtil.bytesFromDecimal(tx.gasLimit), 16);
        alg = ALG_SECP256K1;

        hash = getTxHash();
    }

    byte[] getTxHash() {
        return Native.sha3256(new byte[][]{
                from,
                to,
                value,
                padData(BCUtil.bytesFromDecimal(new BigDecimal(nonce)), 8),
                padData(BCUtil.bytesFromDecimal(new BigDecimal(timestamp)), 8),
                getProtoData(data),
                padData(BCUtil.bytesFromDecimal(new BigDecimal(chainID)), 4),
                gasPrice,
                gasLimit
        });
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


        byte[] data = builder.build().toByteArray();
        return Base64.encodeToString(data, 0).replaceAll("[\\s*\t\n\r]", "");
    }

    private byte[] padData(byte[] data, int len) {
        byte[] result = new byte[len];
        int min = Math.min(data.length, len);
        System.arraycopy(data, data.length - min, result, result.length - min, min);
        return result;
    }

    private byte[] getProtoData(NebData data) {
        ProtoTransaction.Data.Builder dataBuilder = ProtoTransaction.Data.newBuilder();
        dataBuilder.setType(data.type);
        if (data.payload != null) {
            dataBuilder.setPayload(ByteString.copyFrom(data.payload));
        }
        return dataBuilder.build().toByteArray();
    }

}
