package com.example.springaicrud.auth;

import com.example.springaicrud.repository.UserRepository;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
// ✅ Remove @RequiredArgsConstructor
// ✅ Use @Lazy on JwtFilter injection
public class SecurityConfig {

        private final UserRepository userRepository;

        // ✅ @Lazy breaks the circular dependency
        // JwtFilter will be created AFTER SecurityConfig
        private final JwtFilter jwtFilter;

        public SecurityConfig(
                        UserRepository userRepository,
                        @Lazy JwtFilter jwtFilter) {
                this.userRepository = userRepository;
                this.jwtFilter = jwtFilter;
        }

        // ✅ UserDetailsService as a Bean
        // loads user from DB by email
        public UserDetailsService userDetailsService() {
                return email -> userRepository
                                .findByEmail(email)
                                .map(u -> User
                                                .withUsername(u.getEmail())
                                                .password(u.getPassword())
                                                // ✅ get role name from Role object
                                                .roles(u.getRole().getName())
                                                .build())
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "User not found: " + email));
        }

        // ✅ BCrypt password encoder
        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        // ✅ Security rules
        // ✅ Add security headers to filterChain
        @Bean
        public SecurityFilterChain filterChain(
                        HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(s -> s
                                                .sessionCreationPolicy(
                                                                SessionCreationPolicy.STATELESS))

                                // ✅ Security Headers
                                .headers(headers -> headers
                                                // prevent clickjacking
                                                .frameOptions(frame -> frame.deny())
                                                // prevent MIME sniffing
                                                .contentTypeOptions(content -> content.disable())
                                                // XSS protection
                                                .xssProtection(xss -> xss.disable()))

                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                "/api/auth/**",
                                                                "/api/persons/*/image",
                                                                "/api/categories/**",
                                                                "/api/books/**",
                                                                "/",
                                                                "/index.html",
                                                                "/**/*.html",
                                                                "/**/*.css",
                                                                "/**/*.js")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .addFilterBefore(
                                                jwtFilter,
                                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}