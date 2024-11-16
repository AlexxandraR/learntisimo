package com.asos.reservationSystem.config;

import com.asos.reservationSystem.controllers.UserController;
import com.asos.reservationSystem.repositories.UserRepository;
import com.asos.reservationSystem.token.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        Logger logger = LoggerFactory.getLogger(UserController.class);

        try {
            final String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return;
            }

            String jwt = authHeader.substring(7);
            String userEmail = jwtService.extractUsername(jwt);

            var user = userRepository.findUserByEmail(userEmail);
            if (user.isEmpty()) {
                return;
            }

            Long userId = user.get().getId();
            var storedTokens = tokenRepository.findAllValidTokenByUser(userId);

            for (var storedToken : storedTokens) {
                storedToken.setExpired(true);
                storedToken.setRevoked(true);
                tokenRepository.save(storedToken);
            }

            logger.info("Logout: Successfully logged out user with id: " + user.get().getId() + " at: "
                    + LocalDateTime.now());
            SecurityContextHolder.clearContext();
        }catch (Exception e){
            logger.info("Logout: Logout failed for user with email: "
                    + jwtService.extractUsername(request.getHeader("Authorization").substring(7))
                    + " at: " + LocalDateTime.now());
        }
    }
}
