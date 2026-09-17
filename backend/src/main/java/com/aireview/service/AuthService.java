package com.aireview.service;

import com.aireview.dto.LoginRequest;
import com.aireview.dto.LoginResponse;
import com.aireview.dto.RegisterRequest;
import com.aireview.dto.UserVO;

public interface AuthService {
    /** 注册成功即签发令牌，前端无需再调一次登录。 */
    LoginResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    UserVO currentUser();
}
