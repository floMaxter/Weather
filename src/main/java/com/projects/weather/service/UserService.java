package com.projects.weather.service;

import com.projects.weather.model.User;
import com.projects.weather.repository.UserRepository;
import com.projects.weather.security.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("The user with this id was not found: " + id));
    }

    @Transactional(readOnly = true)
    public User findByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("The user with this login was not found: " + login));
    }

    @Transactional
    public void create(String login, String password) {
        userRepository.save(User.builder()
                .login(login)
                .password(passwordEncoder.encode(password))
                .build());
    }

    @Transactional
    public void delete(Long id) {
        userRepository.delete(id);
    }

    @Transactional
    public void deleteAll() {
        userRepository.deleteAll();
    }
}
