package com.projects.weather.controller;

import com.projects.weather.dto.UserDto;
import com.projects.weather.mapper.UserMapper;
import com.projects.weather.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping
    public String getAllUsers(Model model) {
        var users = userService.findAll();
        model.addAttribute("users", userMapper.toDto(users));
        return "user/users";
    }

    @GetMapping("/{id}")
    public String getUserById(@PathVariable("id") Long id, Model model) {
        var user = userService.findById(id);
        model.addAttribute("user", userMapper.toDto(user));
        return "user/user";
    }

    @PostMapping
    public String createUser(@ModelAttribute("user") UserDto userDto) {
        userService.save(userMapper.toEntity(userDto));
        return "redirect:/users";
    }
}
