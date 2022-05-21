package com.pweb.backend.utils;

import com.pweb.backend.model.User;
import com.pweb.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class DebugDatabaseMock {
    @Autowired
    UserRepository userRepository;

    @Bean
    public CommandLineRunner loadData() {
        return (args) -> {
            User nilUser = new User();
            nilUser.setIdentity("nil");
            nilUser.setName("nil");
            nilUser.setEmail("nil");
            if (this.userRepository.findByIdentity("nil").isEmpty()) {
                this.userRepository.save(nilUser);
            }
        };
    }
}
