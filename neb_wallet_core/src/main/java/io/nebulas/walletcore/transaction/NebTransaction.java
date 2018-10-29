package io.nebulas.walletcore.transaction;

import java.math.BigDecimal;

public class NebTransaction<T> {
    public int chainID;
    public String from;
    public String to;
    public BigDecimal value;
    public long nonce;
    public T data;
    public BigDecimal gasPrice;
    public BigDecimal gasLimit;
}
