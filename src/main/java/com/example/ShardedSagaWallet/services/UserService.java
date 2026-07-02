package com.example.ShardedSagaWallet.services;

import java.util.List;
import java.util.Optional;

// Removed invalid import
import org.springframework.stereotype.Service;

import com.example.ShardedSagaWallet.entities.User;
import com.example.ShardedSagaWallet.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
  private final UserRepository userRepository;
 
   public User createUser(User user) {
    log.info("Creating user: {}", user.getEmail());
    User newUser= userRepository.save(user);
    log.info("User created with ID {} in database shaedwallet{}", newUser.getId(), (newUser.getId()%2 + 1));

    return newUser;
  }

  public Optional<User> getUserById(Long id) {
    log.info("Fetching user with ID: {}", id);
    return userRepository.findById(id);
  }

  public List<User> getAllUsers() {
    log.info("Fetching all users");
    return userRepository.findAll();
  }
  
  public List<User> getUserByName(String name) {
    log.info("Fetching users with name: {}", name);
    return userRepository.findByNameIgnoreCase(name);
  }
}
