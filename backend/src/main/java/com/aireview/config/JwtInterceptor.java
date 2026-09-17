package com.aireview.config;

import com.aireview.common.Result;
import com.aireview.common.ResultCode;
import com.aireview.util.JwtUtil;
import com.aireview.util.UserContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerInterceptor;

public class JwtInterceptor implements HandlerInterceptor {
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    public JwtInterceptor(JwtUtil jwtUtil, ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws IOException {
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            writeUnauthorized(response);
            return false;
        }
        Long userId = jwtUtil.parseUserId(header.substring(BEARER_PREFIX.length()));
        if (userId == null) {
            writeUnauthorized(response);
            return false;
        }
        UserContext.set(userId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
        Exception exception) {
        // 线程复用，必须清理，否则会串号
        UserContext.clear();
    }

    private void writeUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(ResultCode.UNAUTHORIZED.code());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(
            Result.fail(ResultCode.UNAUTHORIZED.code(), ResultCode.UNAUTHORIZED.message())));
    }
}
