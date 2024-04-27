package com.unisalle.universalLogin.securityConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unisalle.universalLogin.dtos.AuthenticationRequest;
import com.unisalle.universalLogin.dtos.AuthenticationResponse;

import com.unisalle.universalLogin.services.AuthenticationManagerService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
        String email = "", password = "";
        try {
            AuthenticationRequest authRequest = mapper.readValue(request.getInputStream(), AuthenticationRequest.class);
            email = authRequest.getEmail();
            password = authRequest.getPassword();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(email, password));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult)
            throws IOException, ServletException
    {
        //We fetch the user details and create the authentication response
        UserDetailsImp userD = (UserDetailsImp )authResult.getPrincipal();
        UserDetails userDetails = userDetailsService.loadUserByUsername(userD.getUsername());
        Set<String> grantedAuthorities =  userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());//ERROR EHRERERER
        grantedAuthorities.forEach(System.out::println);

        Map<String, Object> authorities = new HashMap<>();
        authorities.put("authorities", grantedAuthorities);

        String accessToken = jwtUtils.generateJwtToken(userDetails, authorities);
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

