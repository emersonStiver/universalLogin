package com.unisalle.universalLogin.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unisalle.universalLogin.dtos.AuthenticationResponse;
import com.unisalle.universalLogin.entities.Token;
import com.unisalle.universalLogin.entities.TokenType;
import com.unisalle.universalLogin.entities.UserEntity;
import com.unisalle.universalLogin.repositories.TokenRepository;
import com.unisalle.universalLogin.repositories.UserRepository;
import com.unisalle.universalLogin.securityConfig.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class AuthenticationManagerService {
    private TokenRepository tokenRepository;
    private JwtUtils jwtUtils;
    private UserRepository userRepository;
    public AuthenticationManagerService(TokenRepository tokenRepository, JwtUtils jwtUtils, UserRepository userRepository){
        this.tokenRepository = tokenRepository;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
    }
    /*
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException{
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtUtils.extractUsernamme(refreshToken);
        if (userEmail != null) {
            var user = userRepository.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtUtils.isTokenValid(refreshToken, user)) {
                var accessToken = jwtUtils.generateJwtToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
     */

    public void saveUserToken(UserDetails userDetails, String accessToken, String refreshToken){
        UserEntity user = userRepository.findByUserId(userDetails.getUsername()).get();
        Token token = Token.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(user)
                .tokenType(TokenType.BEARER)
                .isExpired(false)
                .isRevoked(false)
                .build();
        tokenRepository.save(token);
    }
    public void revokeAllUserTokens(UserDetails userDetails){
        //List<Token> tokens = tokenRepository.findAllValidTokenByUser(1L);
        List<Token> tokens = List.of();
        if(tokens.isEmpty()) return;
        for(Token token : tokens){
            token.setRevoked(true);
            token.setExpired(true);
        }
        tokenRepository.saveAll(tokens);
    }

}
