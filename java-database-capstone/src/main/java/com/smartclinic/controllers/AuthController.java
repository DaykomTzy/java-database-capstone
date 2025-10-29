package com.smartclinic.controllers;

import com.smartclinic.config.JwtUtil;
import com.smartclinic.models.mysql.User;
import com.smartclinic.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Optional<User> user = userService.getUserByUsername(userDetails.getUsername());
            
            if (user.isEmpty()) {
                return ResponseEntity.badRequest().body("User not found");
            }
            
            String jwt = jwtUtil.generateToken(userDetails.getUsername(), user.get().getRole().name());
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("username", user.get().getUsername());
            response.put("email", user.get().getEmail());
            response.put("role", user.get().getRole());
            response.put("firstName", user.get().getFirstName());
            response.put("lastName", user.get().getLastName());
            response.put("userId", user.get().getId());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid username or password");
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            if (userService.getUserByUsername(registerRequest.getUsername()).isPresent()) {
                return ResponseEntity.badRequest().body("Username already exists");
            }
            
            if (userService.getUserByEmail(registerRequest.getEmail()).isPresent()) {
                return ResponseEntity.badRequest().body("Email already exists");
            }
            
            User user = new User();
            user.setUsername(registerRequest.getUsername());
            user.setEmail(registerRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setFirstName(registerRequest.getFirstName());
            user.setLastName(registerRequest.getLastName());
            user.setRole(registerRequest.getRole());
            user.setPhone(registerRequest.getPhone());
            
            User savedUser = userService.createUser(user);
            
            String jwt = jwtUtil.generateToken(savedUser.getUsername(), savedUser.getRole().name());
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("username", savedUser.getUsername());
            response.put("email", savedUser.getEmail());
            response.put("role", savedUser.getRole());
            response.put("firstName", savedUser.getFirstName());
            response.put("lastName", savedUser.getLastName());
            response.put("userId", savedUser.getId());
            response.put("message", "User registered successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        }
    }
    
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body("Invalid authorization header");
            }
            
            String token = authHeader.substring(7);
            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.extractUsername(token);
                Optional<User> user = userService.getUserByUsername(username);
                
                if (user.isPresent()) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("valid", true);
                    response.put("username", user.get().getUsername());
                    response.put("role", user.get().getRole());
                    response.put("userId", user.get().getId());
                    return ResponseEntity.ok(response);
                }
            }
            
            return ResponseEntity.badRequest().body("Invalid token");
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Token validation failed");
        }
    }
    
    public static class LoginRequest {
        private String username;
        private String password;
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    
    public static class RegisterRequest {
        private String username;
        private String email;
        private String password;
        private String firstName;
        private String lastName;
        private String phone;
        private User.Role role;
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public User.Role getRole() { return role; }
        public void setRole(User.Role role) { this.role = role; }
    }
}

