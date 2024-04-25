package com.unisalle.universalLogin.securityConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unisalle.universalLogin.dtos.AuthenticationRequest;
import com.unisalle.universalLogin.dtos.AuthenticationResponse;

import com.unisalle.universalLogin.services.AuthenticationManagerService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    public JwtAuthenticationFilter(JwtUtils jwtUtils, ObjectMapper mapper, UserDetailsService userDetailsService, AuthenticationManagerService authenticationManagerService, AuthenticationManager manager) {
        this.jwtUtils = jwtUtils;
        this.mapper = mapper;
        this.userDetailsService = userDetailsService;
        this.authenticationManagerService = authenticationManagerService;
        super.setFilterProcessesUrl("/login");
        super.setAuthenticationManager(manager);
    }
    private ObjectMapper mapper;
    private JwtUtils jwtUtils;
    private UserDetailsService userDetailsService;
    private AuthenticationManagerService authenticationManagerService;


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException{
        String username = "", password = "";
        try {
            AuthenticationRequest authRequest = mapper.readValue(request.getInputStream(), AuthenticationRequest.class);
            username = authRequest.getUsername();
            password = authRequest.getPassword();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult)
            throws IOException, ServletException
    {
        //We fetch the user details and create the authentication response
        UserDetails userDetails = userDetailsService.loadUserByUsername(String.valueOf(authResult.getPrincipal()));
        String accessToken = jwtUtils.generateJwtToken(userDetails);
        String refreshToken = jwtUtils.generateJwtRefreshToken(userDetails);
        var res = AuthenticationResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();

        //We revoke previous access tokens and save the new access token + user details
        authenticationManagerService.revokeAllUserTokens(userDetails);
        authenticationManagerService.saveUserToken(userDetails, accessToken, refreshToken);


        //We write the authentication response to the http response
        response.getWriter().write(mapper.writeValueAsString(res));
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().flush();

        //this creates the securityContext and execute the doFilter() method
        super.successfulAuthentication(request, response,chain, authResult);
    }


}

