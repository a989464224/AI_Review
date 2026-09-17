package com.aireview.controller;

import com.aireview.common.Result;
import com.aireview.dto.LoginRequest;
import com.aireview.dto.LoginResponse;
import com.aireview.dto.RegisterRequest;
import com.aireview.dto.UserVO;
import com.aireview.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public Result<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        return Result.ok(authService.register(request));
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.ok(authService.login(request));
    }

    @GetMapping("/me")
    public Result<UserVO> me() {
        return Result.ok(authService.currentUser());
    }
}
