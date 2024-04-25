package com.unisalle.universalLogin.securityConfig;

import com.unisalle.universalLogin.repositories.TokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter{
    public JwtAuthorizationFilter(JwtUtils jwtUtils, UserDetailsService userDetailsService, TokenRepository tokenRepository){
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
        this.tokenRepository = tokenRepository;
    }
    private JwtUtils jwtUtils;
    private UserDetailsService userDetailsService;
    private TokenRepository tokenRepository;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{

        //continue the chain if path contains /login
        if(request.getServletPath().contains("/login")){
            filterChain.doFilter(request, response);
            return;
        }

        //If no authorization header is set or the value x start with Bearer, deny authorization
        final String authorizationHeader = request.getParameter("Authorization");
        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer")){
            filterChain.doFilter(request, response);
            return;
        }

        final String username = jwtUtils.extractUsernamme(authorizationHeader.substring(7));
        final String jwt = authorizationHeader.substring(7);

        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            boolean isTokenValid = tokenRepository.findByAccessToken(jwt).map(token -> !token.isRevoked() && !token.isExpired()).orElse(false);

            if(isTokenValid && jwtUtils.isTokenValid(jwt, userDetails)){
                var authenticationToken =  UsernamePasswordAuthenticationToken.authenticated(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
                WebAuthenticationDetails webAuthenticationDetails = new WebAuthenticationDetailsSource().buildDetails(request);
                authenticationToken.setDetails(webAuthenticationDetails);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request,response);


    }
}
