package com.financeiro.api.controllers;

import com.financeiro.api.dtos.LoginDTO;
import com.financeiro.api.dtos.RegisterDTO;
import com.financeiro.api.models.User;
import com.financeiro.api.repositories.UserRepository;
import com.financeiro.api.services.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterDTO data) {
        if (this.userRepository.findByUsername(data.username()) != null) {
            throw new RuntimeException("Nome de usuário indisponível.");
        }

        String encryptedPassword = passwordEncoder.encode(data.password());

        User newUser = User.builder()
                .username(data.username())
                .passwordHash(encryptedPassword)
                .profilePictureUrl(data.profilePictureUrl())
                .build();

        this.userRepository.save(newUser);

        return ResponseEntity.status(201).build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.username(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);
        var token = tokenService.generateToken((User) auth.getPrincipal());

        ResponseCookie jwtCookie = ResponseCookie.from("jwt_token", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(2 * 60 * 60)
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .build();
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(401).build();
        }

        User user = (User) authentication.getPrincipal();
        User dbUser = userRepository.findById(user.getId()).orElseThrow();

        java.util.Map<String, Object> userData = new java.util.HashMap<>();
        userData.put("id", dbUser.getId());
        userData.put("username", dbUser.getUsername());
        userData.put("profilePictureUrl", dbUser.getProfilePictureUrl());
        userData.put("isOnboarded", dbUser.getIsOnboarded());

        return ResponseEntity.ok(userData);
    }

    @PutMapping("/onboard")
    public ResponseEntity<?> completeOnboarding() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        
        User dbUser = userRepository.findById(user.getId()).orElseThrow();
        dbUser.setIsOnboarded(true);
        userRepository.save(dbUser);
        
        return ResponseEntity.ok().build();
    }
}