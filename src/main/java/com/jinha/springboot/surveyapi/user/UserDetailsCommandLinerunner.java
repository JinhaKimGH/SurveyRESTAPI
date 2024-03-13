package com.jinha.springboot.surveyapi.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserDetailsCommandLinerunner implements CommandLineRunner {

    public UserDetailsCommandLinerunner(UserDetailsRepository repository) {
        this.repository = repository;
    }

    private Logger logger = LoggerFactory.getLogger(getClass());

    private UserDetailsRepository repository;

    @Override
    public void run(String... args) throws Exception {
        repository.save(new UserDetails("Jinha", "Admin"));
        repository.save(new UserDetails("Jeffrey", "Admin"));
        repository.save(new UserDetails("Jadon", "User"));

        //List<UserDetails> users = repository.findAll();

        List<UserDetails> users = repository.findByRole("Admin");

        users.forEach(user -> logger.info(user.toString()));
    }

}
