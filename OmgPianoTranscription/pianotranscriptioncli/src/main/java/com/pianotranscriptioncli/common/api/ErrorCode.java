package com.pianotranscriptioncli.common.api;

/**
 * 封装API的错误码
 *
 */
public interface ErrorCode {
    long getCode();

    String getMessage();
}