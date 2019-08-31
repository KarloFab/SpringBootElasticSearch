package com.karlo.elasticsearch.dao;

import com.karlo.elasticsearch.domain.User;

import java.util.List;

public interface UserDAO {

    List<User> findAllUsers();
    User findById(String userId);
    User save(User user);
    Object findAllUserSettings(String userId);
    String findUserSettings(String userId, String key);
    String saveUserSettings(String userId, String key, String value);
}
