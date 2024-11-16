package com.asos.reservationSystem.auth;

import com.asos.reservationSystem.config.JwtService;
import com.asos.reservationSystem.controllers.UserController;
import com.asos.reservationSystem.domain.entities.User;
import com.asos.reservationSystem.exception.CustomException;
import com.asos.reservationSystem.repositories.UserRepository;
import com.asos.reservationSystem.token.Token;
import com.asos.reservationSystem.token.TokenRepository;
import com.asos.reservationSystem.token.TokenType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.asos.reservationSystem.domain.entities.Role;

import java.io.IOException;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        Logger logger = LoggerFactory.getLogger(UserController.class);
        try {
            var user = User.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .phoneNumber(request.getPhoneNumber())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(Role.STUDENT)
                    .build();
            var savedUser = repository.save(user);
            var jwtToken = jwtService.generateToken(user);
            var refreshedToken = jwtService.generateRefreshToken(user);
            saveUserToken(savedUser, jwtToken, true);
            saveUserToken(savedUser, refreshedToken, false);
            logger.info("Register: Successfully registered user with id: " + user.getId() + " at: "
                    + LocalDateTime.now());
            return AuthenticationResponse.builder().accessToken(jwtToken).refreshToken(refreshedToken).build();
        }catch (Exception e){
            throw new CustomException("Registration failed.", "Registration: Registration failed for "
                    + "user with email: " + request.getEmail() + ".",
                    HttpStatus.UNAUTHORIZED);
        }
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        Logger logger = LoggerFactory.getLogger(UserController.class);
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(),
                    request.getPassword()));
        }catch (AuthenticationException e){
            throw new CustomException("Authentication failed.", "Authentication: Authentication failed for "
                    + "user with email: " + request.getEmail() + ".",
                    HttpStatus.UNAUTHORIZED);
        }
        var user = repository.findUserByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var refreshedToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken, true);
        saveUserToken(user, refreshedToken, false);
        logger.info("Authenticate: Successfully authenticated user with id: " + user.getId() + " at: "
                + LocalDateTime.now());
        return AuthenticationResponse.builder().accessToken(jwtToken).refreshToken(refreshedToken).build();
    }

    public void saveUserToken(User user, String jwtToken, boolean access) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .access(access)
                .build();
        tokenRepository.save(token);
    }

    public void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    private void revokeAllUserAccessTokens(User user) {
        var validUserTokens = tokenRepository.findAllAccessValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.repository.findUserByEmail(userEmail)
                    .orElseThrow();
            var isTokenValid = tokenRepository.findByToken(refreshToken)
                    .map(t -> !t.isExpired() && !t.isRevoked())
                    .orElse(false);
            if (jwtService.isTokenValid(refreshToken, user) && isTokenValid) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserAccessTokens(user);
                saveUserToken(user, accessToken, true);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            } else {
                return;
            }
        }
    }
}
