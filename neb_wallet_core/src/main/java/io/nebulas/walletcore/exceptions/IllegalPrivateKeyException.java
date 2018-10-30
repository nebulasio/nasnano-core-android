package io.nebulas.walletcore.exceptions;

public class IllegalPrivateKeyException extends NebulasException {
    @Override
    public String getMessage() {
        return "不合法的私钥";
    }
}
