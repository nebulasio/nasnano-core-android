package io.nebulas.walletcore.transaction;

import java.math.BigDecimal;

public class NebTransaction {
    public int chainID;
    public String from;
    public String to;
    public BigDecimal value;
    public long nonce;
    /**
     * NebBinaryData 或者 NebCallData 或者 NebDeployData
     */
    public Object data;
    public BigDecimal gasPrice;
    public BigDecimal gasLimit;
}
