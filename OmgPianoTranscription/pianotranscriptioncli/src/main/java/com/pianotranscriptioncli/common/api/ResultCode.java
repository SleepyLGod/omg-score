package com.pianotranscriptioncli.common.api;

public enum ResultCode implements ErrorCode {

    SUCCESS(1, "操作成功"),
    FAILED(-1, "操作失败"),
    VALIDATE_FAILED(101, "参数检验失败"),
    UNAUTHORIZED(102, "暂未登录或token已经过期"),
    FORBIDDEN(103, "没有相关权限");

    private final long code;
    private final String message;

    ResultCode(long code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public long getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
