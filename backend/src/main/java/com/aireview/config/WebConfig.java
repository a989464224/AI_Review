package com.aireview.config;

import com.aireview.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    /** 无需登录即可访问的路径。新增公开接口时必须显式加到这里。 */
    static final String[] PUBLIC_PATHS = {
        "/api/health",
        "/api/auth/register",
        "/api/auth/login"
    };

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    public WebConfig(JwtUtil jwtUtil, ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JwtInterceptor(jwtUtil, objectMapper))
            .addPathPatterns("/api/**")
            .excludePathPatterns(PUBLIC_PATHS);
    }
}
