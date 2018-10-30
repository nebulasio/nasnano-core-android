package io.nebulas.walletcore.exceptions;

public class IllegalPrivateKeyException extends NebulasException {

    public IllegalPrivateKeyException() {
        super(ErrorOverview.IllegalPrivateKey.errorCode, ErrorOverview.IllegalPrivateKey.errorMessage);
    }
}
