package com.aireview.service.impl;

import com.aireview.common.BusinessException;
import com.aireview.common.ResultCode;
import com.aireview.dto.LoginRequest;
import com.aireview.dto.LoginResponse;
import com.aireview.dto.RegisterRequest;
import com.aireview.dto.UserVO;
import com.aireview.entity.User;
import com.aireview.mapper.UserMapper;
import com.aireview.service.AuthService;
import com.aireview.util.JwtUtil;
import com.aireview.util.UserContext;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public LoginResponse register(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.username());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setNickname(request.nickname() == null || request.nickname().isBlank()
            ? request.username()
            : request.nickname());
        try {
            userMapper.insert(user);
        } catch (DuplicateKeyException exception) {
            // 并发下先查后插仍可能撞唯一索引，以索引为准
            throw new BusinessException(ResultCode.USERNAME_TAKEN);
        }
        // 回读一次拿到数据库生成的 created_at，避免返回 null
        return issueToken(userMapper.selectById(user.getId()));
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = findByUsername(request.username());
        // 用户不存在与密码错误返回同一提示，避免用户名枚举
        if (user == null || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BusinessException(ResultCode.LOGIN_FAILED);
        }
        return issueToken(user);
    }

    @Override
    public UserVO currentUser() {
        User user = userMapper.selectById(UserContext.require());
        if (user == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return toVO(user);
    }

    private User findByUsername(String username) {
        return userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, username));
    }

    private LoginResponse issueToken(User user) {
        return new LoginResponse(jwtUtil.generateToken(user.getId()), toVO(user));
    }

    private UserVO toVO(User user) {
        return new UserVO(user.getId(), user.getUsername(), user.getNickname(), user.getCreatedAt());
    }
}
