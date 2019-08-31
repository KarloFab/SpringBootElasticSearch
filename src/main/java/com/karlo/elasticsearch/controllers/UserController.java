package com.karlo.elasticsearch.controllers;

import com.karlo.elasticsearch.dao.UserDAO;
import com.karlo.elasticsearch.domain.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    private final UserDAO userDAO;

    public UserController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @GetMapping("/users")
    public List<User> getUsers(){
        return userDAO.findAllUsers();
    }

    @PostMapping("/users")
    public User addUsers(@RequestBody User user) {
        userDAO.save(user);
        return user;
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable String id){
        return userDAO.findById(id);
    }

    @GetMapping("/users/{id}/settings")
    public Object getAllUserSettings(@PathVariable String id){
        return userDAO.findAllUserSettings(id);
    }

    @GetMapping("/users/{id}/settings/{key}")
    public Object getUserSettings(@PathVariable String id, @PathVariable String key){
        return userDAO.findUserSettings(id, key);
    }
}
