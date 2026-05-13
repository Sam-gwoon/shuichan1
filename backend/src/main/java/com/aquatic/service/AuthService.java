package com.aquatic.service;

import com.aquatic.dto.LoginRequest;
import com.aquatic.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
