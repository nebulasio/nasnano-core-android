package io.nebulas.walletcore.exceptions;

public class IllegalKeystoreException extends NebulasException {
    @Override
    public String getMessage() {
        return "不合法的Keystore";
    }
}
