package com.pweb.backend.service;

import com.pweb.backend.model.User;
import com.pweb.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isValidRequest(Map<String, Object> request) {
        return ((request.containsKey("name")) &&
                (request.containsKey("email")));
    }

    public boolean isPresent(String identity) {
        return this.userRepository.findByIdentity(identity).isPresent();
    }

    public Long findIdByIdentity(String identity) {
        return this.userRepository.findByIdentity(identity).orElseThrow().getId();
    }

    public User findByIdentity(String identity) {
        return this.userRepository.findByIdentity(identity).orElseThrow();
    }

    public User findById(Long id) {
        return this.userRepository.findById(id).orElseThrow();
    }

    public void save(User user) {
        this.userRepository.save(user);
    }
}
