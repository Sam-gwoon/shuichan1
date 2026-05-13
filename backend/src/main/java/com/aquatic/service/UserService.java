package com.aquatic.service;

import com.aquatic.entity.User;
import java.util.List;

public interface UserService {
    List<User> listAll();
    User getById(Long id);
    User getByUsername(String username);
    void create(User user);
    void update(User user);
    void delete(Long id);
}
