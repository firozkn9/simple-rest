package io.khan.rest.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.khan.rest.model.User;
import io.khan.rest.repository.UserRepository;

@RestController
@RequestMapping(value="/rest/api/v1")
public class UserRestController {
    private static final Logger logger = LoggerFactory.getLogger(UserRestController.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
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

    @GetMapping(value="/user/{name}", produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getUserByName(@PathVariable String name) {
        if (name == null || name.trim().isEmpty()) {
            logger.warn("Name parameter is empty");
            return ResponseEntity.badRequest().body("Name is required");  // 400
        }
        
        try {
            User user = userRepository.findByNameIgnoreCase(name);
            if (user == null) {
                logger.info("User not found with name: {}", name);
                return ResponseEntity.notFound().build();  // 404 Not Found
            }
            String jsonResponse = objectMapper.writeValueAsString(user);
            return ResponseEntity.ok(jsonResponse);  // 200 OK
        } catch (Exception e) {
            logger.error("Error retrieving user by name", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();  // 500
        }
    }

    @PostMapping(value="/user")
    public ResponseEntity<String> saveUser(@RequestBody User user) {
        if (user == null) {
            logger.warn("User object is null");
            return ResponseEntity.badRequest().body("User object cannot be null");  // 400
        }
        
        try {
            User savedUser = userRepository.save(user);
            logger.info("User saved successfully with id: {}", savedUser.getId());
            String jsonResponse = objectMapper.writeValueAsString(savedUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(jsonResponse);  // 201 Created
        } catch (IllegalArgumentException e) {
            logger.error("Invalid user data", e);
            return ResponseEntity.badRequest().body("Invalid user data");  // 400
        } catch (Exception e) {
            logger.error("Error saving user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving user: " + e.getMessage());  // 500
        }
    }

    @PutMapping(value="/user/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody User user) {
        if (id == null || id <= 0) {
            logger.warn("Invalid user ID: {}", id);
            return ResponseEntity.badRequest().body("User ID must be valid");  // 400
        }
        
        if (user == null) {
            logger.warn("User object is null");
            return ResponseEntity.badRequest().body("User object cannot be null");  // 400
        }
        
        try {
            User existingUser = userRepository.findById(id).orElse(null);
            if (existingUser == null) {
                logger.info("User not found with id: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with id: " + id);  // 404 Not Found
            }
            
            // Update user fields
            if (user.getName() != null && !user.getName().trim().isEmpty()) {
                existingUser.setName(user.getName());
            }
            if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
                existingUser.setEmail(user.getEmail());
            }
            
            User updatedUser = userRepository.save(existingUser);
            logger.info("User updated successfully with id: {}", id);
            String jsonResponse = objectMapper.writeValueAsString(updatedUser);
            return ResponseEntity.ok(jsonResponse);  // 200 OK
        } catch (IllegalArgumentException e) {
            logger.error("Invalid user data", e);
            return ResponseEntity.badRequest().body("Invalid user data");  // 400
        } catch (Exception e) {
            logger.error("Error updating user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating user: " + e.getMessage());  // 500
        }
    }

    @DeleteMapping(value="/user/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        if (id == null || id <= 0) {
            logger.warn("Invalid user ID: {}", id);
            return ResponseEntity.badRequest().body("User ID must be valid");  // 400
        }
        
        try {
            User existingUser = userRepository.findById(id).orElse(null);
            if (existingUser == null) {
                logger.info("User not found with id: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with id: " + id);  // 404 Not Found
            }
            
            userRepository.deleteById(id);
            logger.info("User deleted successfully with id: {}", id);
            return ResponseEntity.noContent().build();  // 204 No Content
        } catch (Exception e) {
            logger.error("Error deleting user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting user: " + e.getMessage());  // 500
        }
    }
}