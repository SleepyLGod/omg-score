package com.pianotranscriptioncli.common.exception;

import com.pianotranscriptioncli.common.api.ErrorCode;

public class Asserts extends org.springframework.util.Assert {
    public static void fail(String message) {
        throw new BaseException(message);
    }

    public static void fail(ErrorCode errorCode) {
        throw new BaseException(errorCode);
    }
}
