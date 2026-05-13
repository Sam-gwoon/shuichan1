package com.aquatic.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private Long userId;
    private String username;
    private String realName;
    private String role;
    private String employeeNo;

    public LoginResponse(String token, Long userId, String username, String realName, String role, String employeeNo) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.realName = realName;
        this.role = role;
        this.employeeNo = employeeNo;
    }
}
