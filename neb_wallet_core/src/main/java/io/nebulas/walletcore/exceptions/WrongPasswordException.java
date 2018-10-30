package io.nebulas.walletcore.exceptions;

public class WrongPasswordException extends NebulasException {
    @Override
    public String getMessage() {
        return "密码错误";
    }
}
