package com.aireview.util;

import com.aireview.common.BusinessException;
import com.aireview.common.ResultCode;

/**
 * 当前登录用户上下文。由 {@code JwtInterceptor} 在请求进入时写入、请求结束时清理，
 * Service 层一律从这里取用户 ID，不信任前端传入的 userId。
 */
public final class UserContext {
    private static final ThreadLocal<Long> CURRENT_USER = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(Long userId) {
        CURRENT_USER.set(userId);
    }

    public static Long get() {
        return CURRENT_USER.get();
    }

    public static Long require() {
        Long userId = CURRENT_USER.get();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return userId;
    }

    public static void clear() {
        CURRENT_USER.remove();
    }
}
