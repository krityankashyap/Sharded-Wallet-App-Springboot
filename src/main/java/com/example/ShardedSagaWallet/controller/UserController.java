package com.example.ShardedSagaWallet.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.example.ShardedSagaWallet.entities.User;
import com.example.ShardedSagaWallet.services.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



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

  @GetMapping()
  public ResponseEntity<List<User>> getAllUser(){ 
      List<User> users= userService.getAllUsers();
      return ResponseEntity.ok(users);
  }

  @GetMapping(":{id}")
  public ResponseEntity<User> getUserById(@RequestParam Long id) {
    return userService.getUserById(id)
        .map(user -> ResponseEntity.ok(user))
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/name")
  public ResponseEntity<List<User>> getUserByName(@RequestParam String name) {
    List<User> users= userService.getUserByName(name);
      return ResponseEntity.ok(users);
  }
  
  
}
