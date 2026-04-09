package com.example.springaicrud.auth;

import com.example.springaicrud.dto.*;
import com.example.springaicrud.entity.*;
import com.example.springaicrud.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password
        .PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository  userRepository;
    private final RoleRepository  roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil         jwtUtil;

    // ==========================================
    // ✅ SIGNUP
    // ==========================================
    public AuthResponse signup(SignupRequest req) {

        // check email exists
        if (userRepository
                .existsByEmail(req.getEmail())) {
            throw new RuntimeException(
                    "Email already registered: "
                            + req.getEmail());
        }

        // find default USER role
        Role userRole = roleRepository
                .findByName("USER")
                .orElseThrow(() ->
                        new RuntimeException(
                                "Role USER not found!"));

        // encrypt password
        String encrypted =
                passwordEncoder.encode(
                        req.getPassword());

        log.info("Signing up: {}", req.getEmail());

        // save user
        User saved = userRepository.save(
                User.builder()
                        .name(req.getName())
                        .email(req.getEmail())
                        .password(encrypted)
                        .role(userRole)
                        .build()
        );

        // ✅ Generate token WITH userId & roleId
        String token = jwtUtil.generateToken(
                saved.getEmail(),           // email
                saved.getId(),              // userId
                saved.getRole().getId(),    // roleId
                saved.getRole().getName()   // roleName
        );

        return AuthResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .email(saved.getEmail())
                .roleId(saved.getRole().getId())
                .roleName(saved.getRole().getName())
                .token(token)
                .message("Signup successful!")
                .loginTime(jwtUtil.extractLoginTime(token))
                .expTime(jwtUtil.extractExpTime(token))
                .build();
    }

    // ==========================================
    // ✅ LOGIN
    // ==========================================
    public AuthResponse login(LoginRequest req) {

        // find user by email
        User user = userRepository
                .findByEmail(req.getEmail())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Email not found: "
                                        + req.getEmail()));

        // compare password
        boolean match = passwordEncoder.matches(
                req.getPassword(),
                user.getPassword());

        if (!match) {
            throw new RuntimeException(
                    "Incorrect password!");
        }

        log.info("Login success: {}",
                req.getEmail());

        // ✅ Generate token WITH userId & roleId
        String token = jwtUtil.generateToken(
                user.getEmail(),            // email
                user.getId(),               // userId
                user.getRole().getId(),     // roleId
                user.getRole().getName()    // roleName
        );

        return AuthResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .roleId(user.getRole().getId())
                .roleName(user.getRole().getName())
                .token(token)
                .message("Login successful!")
                .loginTime(jwtUtil.extractLoginTime(token))
                .expTime(jwtUtil.extractExpTime(token))
                .build();
    }
}