package com.aireview.common;

/**
 * 业务错误。抛出后由 {@link GlobalExceptionHandler} 统一转换为 Result 响应，
 * 业务代码不自行拼装错误返回。
 */
public class BusinessException extends RuntimeException {
    private final int code;

    public BusinessException(ResultCode resultCode) {
        this(resultCode.code(), resultCode.message());
    }

    public BusinessException(ResultCode resultCode, String message) {
        this(resultCode.code(), message);
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
