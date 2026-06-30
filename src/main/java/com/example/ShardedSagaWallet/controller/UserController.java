package com.example.ShardedSagaWallet.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.example.ShardedSagaWallet.entities.User;
import com.example.ShardedSagaWallet.services.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
  
  public final UserService userService;
  
  @PostMapping("/create")
  public ResponseEntity<User> createUser(@RequestBody User user) {
    User newUser= userService.createUser(user);
    return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
  }
}
