package io.nebulas.walletcore.exceptions;

public class IllegalKeystoreException extends NebulasException {
    public IllegalKeystoreException() {
        super(ErrorOverview.IllegalKeystore.errorCode, ErrorOverview.IllegalKeystore.errorMessage);
    }
}
