package com.aquatic.config;

import com.aquatic.entity.User;
import com.aquatic.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 将明文密码自动加密为BCrypt（仅首次运行需要）
        List<User> users = userMapper.selectList(null);
        boolean needUpdate = false;
        for (User user : users) {
            if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                userMapper.updateById(user);
                needUpdate = true;
            }
        }
        if (needUpdate) {
            System.out.println("=== 用户密码已自动加密为BCrypt ===");
        }
    }
}
