package io.nebulas.walletcore.exceptions;

public enum ErrorOverview {

    IllegalPrivateKey(10001, "不合法的私钥"),
    IllegalKeystore(10002, "不合法的Keystore"),
    WrongPassword(10003, "密码错误");

    int errorCode;
    String errorMessage;

    ErrorOverview(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }


}
