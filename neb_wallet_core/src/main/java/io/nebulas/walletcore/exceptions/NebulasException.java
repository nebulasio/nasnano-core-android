package io.nebulas.walletcore.exceptions;

public class NebulasException extends Exception {

    private int errorCode;

    private String message;

    public NebulasException() {
    }

    NebulasException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
