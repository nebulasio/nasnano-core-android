package io.nebulas.walletcore.exceptions;

public class WrongPasswordException extends NebulasException {
    public WrongPasswordException() {
        super(ErrorOverview.WrongPassword.errorCode, ErrorOverview.WrongPassword.errorMessage);
    }
}
