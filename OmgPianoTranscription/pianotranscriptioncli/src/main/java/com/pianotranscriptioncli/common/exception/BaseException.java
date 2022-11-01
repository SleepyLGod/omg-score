package com.pianotranscriptioncli.common.exception;

import com.pianotranscriptioncli.common.api.ErrorCode;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * 自定义API异常
 */
@Getter
@Setter
public class BaseException extends RuntimeException{
    private ErrorCode errorCode;

    public BaseException() {
        super();
    }

    public BaseException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BaseException(String message) {
        super(message);
    }

    public BaseException(Throwable cause) {
        super(cause);
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

}
