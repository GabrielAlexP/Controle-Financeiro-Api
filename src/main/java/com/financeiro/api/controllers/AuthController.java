package com.financeiro.api.controllers;

import com.financeiro.api.dtos.LoginDTO;
import com.financeiro.api.dtos.RegisterDTO;
import com.financeiro.api.dtos.OnboardDTO;
import com.financeiro.api.models.User;
import com.financeiro.api.models.Category;
import com.financeiro.api.models.Account;
import com.financeiro.api.enums.TransactionType;
import com.financeiro.api.repositories.UserRepository;
import com.financeiro.api.repositories.CategoryRepository;
import com.financeiro.api.repositories.AccountRepository;
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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AccountRepository accountRepository;

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

        User savedUser = this.userRepository.save(newUser);

        List<Category> defaultCategories = List.of(
            Category.builder().name("Contas da Casa").type(TransactionType.EXPENSE).colorHex("#EF4444").icon("🏠").user(savedUser).build(),
            Category.builder().name("Alimentação").type(TransactionType.EXPENSE).colorHex("#F59E0B").icon("🍔").user(savedUser).build(),
            Category.builder().name("Uber").type(TransactionType.EXPENSE).colorHex("#3B82F6").icon("🚗").user(savedUser).build()
        );

        categoryRepository.saveAll(defaultCategories);

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

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie jwtCookie = ResponseCookie.from("jwt_token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
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

        Map<String, Object> userData = new HashMap<>();
        userData.put("id", dbUser.getId());
        userData.put("username", dbUser.getUsername());
        userData.put("profilePictureUrl", dbUser.getProfilePictureUrl());
        userData.put("isOnboarded", dbUser.getIsOnboarded());

        return ResponseEntity.ok(userData);
    }

    @PutMapping("/onboard")
    public ResponseEntity<?> completeOnboarding(@RequestBody(required = false) OnboardDTO data) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        
        User dbUser = userRepository.findById(user.getId()).orElseThrow();

        if (data != null && data.accountName() != null && !data.accountName().trim().isEmpty()) {
            Account account = Account.builder()
                .name(data.accountName())
                .balance(data.initialBalance() != null ? data.initialBalance() : BigDecimal.ZERO)
                .user(dbUser)
                .build();
            
            accountRepository.save(account);
        }

        dbUser.setIsOnboarded(true);
        userRepository.save(dbUser);
        
        return ResponseEntity.ok().build();
    }
}