package com.pianotranscriptioncli.common.api;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * 通用返回对象
 *
 */
@Getter
@Setter
public class CommonResult {
    private long code;
    private String message;
    private Object data;

    protected CommonResult() {
    }

    protected CommonResult(long code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功返回结果
     *
     */
    public static CommonResult success() {
        return new CommonResult(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null);
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     */
    public static CommonResult success(Object data) {
        return new CommonResult(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    /**
     * 成功返回结果
     *
     * @param data    获取的数据
     * @param message 提示信息
     */
    public static CommonResult success(Object data, String message) {
        return new CommonResult(ResultCode.SUCCESS.getCode(), message, data);
    }

    /**
     * 失败返回结果
     *
     * @param errorCode 错误码
     */
    public static CommonResult failed(ErrorCode errorCode) {
        return new CommonResult(errorCode.getCode(), errorCode.getMessage(), null);
    }

    /**
     * 失败返回结果
     *
     * @param errorCode 错误码
     * @param message   错误信息
     */
    public static CommonResult failed(ErrorCode errorCode, String message) {
        return new CommonResult(errorCode.getCode(), message, null);
    }

    /**
     * 失败返回结果
     *
     * @param message 提示信息
     */
    public static CommonResult failed(String message) {
        return new CommonResult(ResultCode.FAILED.getCode(), message, null);
    }

    /**
     * 失败返回结果
     */
    public static CommonResult failed() {
        return failed(ResultCode.FAILED);
    }

    /**
     * 参数验证失败返回结果
     */
    public static CommonResult validateFailed() {
        return failed(ResultCode.VALIDATE_FAILED);
    }

    /**
     * 参数验证失败返回结果
     *
     * @param message 提示信息
     */
    public static CommonResult validateFailed(String message) {
        return new CommonResult(ResultCode.VALIDATE_FAILED.getCode(), message, null);
    }

    /**
     * 未登录返回结果
     */
    public static CommonResult unauthorized(Object data) {
        return new CommonResult(ResultCode.UNAUTHORIZED.getCode(), ResultCode.UNAUTHORIZED.getMessage(), data);
    }

    /**
     * 未授权返回结果
     */
    public static CommonResult forbidden(Object data) {
        return new CommonResult(ResultCode.FORBIDDEN.getCode(), ResultCode.FORBIDDEN.getMessage(), data);
    }

    /**
     * 通过map携带简单键值对数据的无限链式调用，但切记不要在data已经不是map类型后调用
     */
    public CommonResult with(String k, Object v) {
        Map<String, Object> map;
        if (data == null) {
            map = new HashMap<>();
        } else {
            map = (Map<String, Object>) this.data;
        }
        map.put(k, v);
        this.data = map;
        return this;
    }
}