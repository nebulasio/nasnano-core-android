package io.nebulas.walletcore.util;

public interface Callback<TR, TP> {
    TR call(TP param);
}
