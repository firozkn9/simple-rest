package io.khan.rest.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.khan.rest.model.User;
import io.khan.rest.repository.UserRepository;

@RestController
@RequestMapping(value="/rest/api/v1")
public class UserRestController {
    private static final Logger logger = LoggerFactory.getLogger(UserRestController.class);
    
    @Autowired
    private UserRepository userRepository;

    @GetMapping(value="/users")
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            if (users.isEmpty()) {
                logger.info("No users found");
                return ResponseEntity.noContent().build();  // 204 No Content
            }
            logger.debug("Retrieved {} users", users.size());
            return ResponseEntity.ok(users);  // 200 OK
        } catch (Exception e) {
            logger.error("Error retrieving users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();  // 500
        }
    }

    @GetMapping(value="/user/{email}", produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        if (email == null || email.trim().isEmpty()) {
            logger.warn("Email parameter is empty");
            return ResponseEntity.badRequest().body("Email is required");  // 400
        }
        
        try {
            User user = userRepository.findByEmail(email);
            if (user == null) {
                logger.info("User not found with email: {}", email);
                return ResponseEntity.notFound().build();  // 404 Not Found
            }
            return ResponseEntity.ok(user);  // 200 OK
        } catch (Exception e) {
            logger.error("Error retrieving user by email", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();  // 500
        }
    }

    @GetMapping(value="/user/{name}", produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUserByName(@PathVariable String name) {
        if (name == null || name.trim().isEmpty()) {
            logger.warn("Name parameter is empty");
            return ResponseEntity.badRequest().body("Name is required");  // 400
        }
        
        try {
            User user = userRepository.findByName(name);
            if (user == null) {
                logger.info("User not found with name: {}", name);
                return ResponseEntity.notFound().build();  // 404 Not Found
            }
            return ResponseEntity.ok(user);  // 200 OK
        } catch (Exception e) {
            logger.error("Error retrieving user by name", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();  // 500
        }
    }

    @PostMapping(value="/user")
    public ResponseEntity<?> saveUser(@RequestBody User user) {
        if (user == null) {
            logger.warn("User object is null");
            return ResponseEntity.badRequest().body("User object cannot be null");  // 400
        }
        
        try {
            User savedUser = userRepository.save(user);
            logger.info("User saved successfully with id: {}", savedUser.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);  // 201 Created
        } catch (IllegalArgumentException e) {
            logger.error("Invalid user data", e);
            return ResponseEntity.badRequest().body("Invalid user data");  // 400
        } catch (Exception e) {
            logger.error("Error saving user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();  // 500
        }
    }
}