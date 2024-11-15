package com.asos.reservationSystem.auth;

import com.asos.reservationSystem.config.JwtService;
import com.asos.reservationSystem.domain.entities.User;
import com.asos.reservationSystem.repositories.UserRepository;
import com.asos.reservationSystem.token.Token;
import com.asos.reservationSystem.token.TokenRepository;
import com.asos.reservationSystem.token.TokenType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.asos.reservationSystem.domain.entities.Role;

import java.io.IOException;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
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
        return AuthenticationResponse.builder().accessToken(jwtToken).refreshToken(refreshedToken).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        var user = repository.findUserByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var refreshedToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken, true);
        saveUserToken(user, refreshedToken, false);
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
                //throw new CustomException("Authorization has expired.", "Authorization: Authorization has expired.", HttpStatus.FORBIDDEN);
                return;
            }
        }
    }
}
