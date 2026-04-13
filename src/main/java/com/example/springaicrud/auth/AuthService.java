package com.example.springaicrud.auth;

import com.example.springaicrud.dto.*;
import com.example.springaicrud.entity.*;
import com.example.springaicrud.repository.*;
import com.example.springaicrud.validation
        .InputValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto
        .password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class AuthService {

    private final UserRepository  userRepository;
    private final RoleRepository  roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil         jwtUtil;
    private final InputValidator  validator;

    // ==========================================
    // ✅ SIGNUP with validation
    // ==========================================
    public AuthResponse signup(
            SignupRequest req) {

        // ✅ Validate all inputs
        validator.validateName(req.getName());
        validator.validateEmail(req.getEmail());
        validator.validatePassword(
                req.getPassword());

        // ✅ Sanitize inputs
        String cleanName =
                validator.sanitize(req.getName());
        String cleanEmail =
                req.getEmail().trim().toLowerCase();

        // check email exists
        if (userRepository
                .existsByEmail(cleanEmail)) {
            throw new RuntimeException(
                    "Email already registered: "
                            + cleanEmail);
        }

        // find USER role
        Role userRole = roleRepository
                .findByName("USER")
                .orElseThrow(() ->
                        new RuntimeException(
                                "Role USER not found!"));

        // encrypt password
        String encrypted =
                passwordEncoder.encode(
                        req.getPassword());

        log.info("Signup: {}", cleanEmail);

        User saved = userRepository.save(
                User.builder()
                        .name(cleanName)
                        .email(cleanEmail)
                        .password(encrypted)
                        .role(userRole)
                        .build()
        );

        String token = jwtUtil.generateToken(
                saved.getEmail(),
                saved.getId(),
                saved.getRole().getId(),
                saved.getRole().getName()
        );

        return AuthResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .email(saved.getEmail())
                .roleId(saved.getRole().getId())
                .roleName(
                        saved.getRole().getName())
                .token(token)
                .loginTime(
                        jwtUtil.extractLoginTime(
                                token))
                .expTime(
                        jwtUtil.extractExpTime(
                                token))
                .message("Signup successful!")
                .build();
    }

    // ==========================================
    // ✅ LOGIN with validation
    // ==========================================
    public AuthResponse login(
            LoginRequest req) {

        // ✅ Validate inputs
        validator.validateEmail(req.getEmail());
        if (req.getPassword() == null
                || req.getPassword().isEmpty()) {
            throw new RuntimeException(
                    "Password is required!");
        }

        String cleanEmail =
                req.getEmail().trim().toLowerCase();

        // find user
        User user = userRepository
                .findByEmail(cleanEmail)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Email not found: "
                                        + cleanEmail));

        // compare password
        boolean match = passwordEncoder.matches(
                req.getPassword(),
                user.getPassword());

        if (!match) {
            log.warn("Failed login attempt: {}",
                    cleanEmail);
            throw new RuntimeException(
                    "Incorrect password!");
        }

        log.info("Login success: {}",
                cleanEmail);

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getId(),
                user.getRole().getId(),
                user.getRole().getName()
        );

        return AuthResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .roleId(user.getRole().getId())
                .roleName(
                        user.getRole().getName())
                .token(token)
                .loginTime(
                        jwtUtil.extractLoginTime(
                                token))
                .expTime(
                        jwtUtil.extractExpTime(
                                token))
                .message("Login successful!")
                .build();
    }
}