package com.aquatic.controller;

import com.aquatic.dto.ApiResult;
import com.aquatic.entity.User;
import com.aquatic.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ApiResult<?> list() {
        return ApiResult.success(userService.listAll());
    }

    @GetMapping("/{id}")
    public ApiResult<User> getById(@PathVariable Long id) {
        return ApiResult.success(userService.getById(id));
    }

    @PostMapping
    public ApiResult<?> create(@RequestBody User user) {
        userService.create(user);
        return ApiResult.success();
    }

    @PutMapping("/{id}")
    public ApiResult<?> update(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        userService.update(user);
        return ApiResult.success();
    }

    @DeleteMapping("/{id}")
    public ApiResult<?> delete(@PathVariable Long id) {
        userService.delete(id);
        return ApiResult.success();
    }
}
