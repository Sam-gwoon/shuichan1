package com.aquatic.controller;

import com.aquatic.dto.ApiResult;
import com.aquatic.dto.LoginRequest;
import com.aquatic.dto.LoginResponse;
import com.aquatic.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ApiResult<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse resp = authService.login(request);
            return ApiResult.success(resp);
        } catch (RuntimeException e) {
            return ApiResult.error(401, e.getMessage());
        }
    }
}
