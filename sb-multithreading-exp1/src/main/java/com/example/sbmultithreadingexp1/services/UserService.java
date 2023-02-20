package com.example.sbmultithreadingexp1.services;


import com.example.sbmultithreadingexp1.entities.User;
import com.example.sbmultithreadingexp1.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Async
    public CompletableFuture<List<User>> saveUsers(MultipartFile file) throws Exception {
        long start = System.currentTimeMillis();
        List<User> users = parseCSVFile(file);
        log.info("saving list of users of size {}, {}", users.size(), Thread.currentThread().getName());
        users = userRepository.saveAll(users);
        long end = System.currentTimeMillis();
        log.info("Total time {}", (end - start));

        return CompletableFuture.completedFuture(users);
    }

    @Async
    public CompletableFuture<List<User>> findAllUsers() {
        log.info("get list of user by " + Thread.currentThread().getName());
        List<User> users = userRepository.findAll();

        return CompletableFuture.completedFuture(users);
    }

    private List<User> parseCSVFile(final MultipartFile file) throws Exception {
        final List<User> users = new ArrayList<>();
        try {
            try (final BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    final String[] data = line.split(",");
                    final User user = new User();
                    user.setName(data[0]);
                    user.setEmail(data[1]);
                    user.setGender(data[2]);
                    users.add(user);
                }
                return users;
            }
        } catch (final IOException e) {
            log.error("Failed to parse CSV file {}", e);
            throw new Exception("Failed to parse CSV file {}", e);
        }
    }
}