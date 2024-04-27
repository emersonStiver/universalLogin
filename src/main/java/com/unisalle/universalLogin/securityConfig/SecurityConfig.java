package com.unisalle.universalLogin.securityConfig;

import com.unisalle.universalLogin.repositories.TokenRepository;
import com.unisalle.universalLogin.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@ComponentScan (value = "com.unisalle.universalLogin")
public class SecurityConfig {
    @Value ("${allowed-post-routes}")
    private String[] allowedPostRoutes;

    //@Value ("${resources}")
    //private String[] resources;


    String resourcesString = "/api/v1/auth/**," +
            "/v2/api-docs," +
            "/v3/api-docs," +
            "/v3/api-docs/**," +
            "/swagger-resources," +
            "/swagger-resources/**," +
            "/configuration/ui," +
            "/configuration/security," +
            "/swagger-ui/**," +
            "/webjars/**," +
            "/swagger-ui.html";

    String[] resources = resourcesString.split(",");
    /*
    @Value("${myapp.user-role-routes}")
    private String[] userRoleRoutes;
    @Value("${myapp.organization-role-routes}")
    private String[] organizationRoleRoutes;
     */
    private UserRepository userRepository;
    private UserDetailsService userDetailsService;
    @Autowired
    public SecurityConfig(UserRepository userRepository, UserDetailsService userDetailsService){
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity security,JwtAuthorizationFilter authorizationFilter, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception{
        String prefix = "/api/v1/users";
        return security
                .authorizeHttpRequests(r -> {
                    r.requestMatchers(HttpMethod.POST,prefix+"/create-user").permitAll();
                    r.requestMatchers(prefix+"/exists/**").permitAll();
                    r.requestMatchers("/login").permitAll();
                    r.requestMatchers("/logout/**").permitAll();
                    r.requestMatchers(resources).permitAll();
                    r.requestMatchers(prefix+"/user-info").hasAuthority("user::read");
                    r.requestMatchers(HttpMethod.PUT,prefix+"/edit").hasAuthority("user::update");
                    r.requestMatchers(prefix+"/delete-user").hasAuthority("user::delete");
                    r.requestMatchers(prefix+"/password").hasAuthority("user::update");
                    r.anyRequest().authenticated();
                })
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authenticationProvider(daoAuthenticationProvider())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilter(jwtAuthenticationFilter)
                .addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider();
        daoProvider.setUserDetailsService(userDetailsService);
        daoProvider.setPasswordEncoder(passwordEncoder());
        return daoProvider;
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }



    @Bean
    public LogoutHandler logout(TokenRepository tokenRepository){
        return (HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> {
            final String authHeader = request.getParameter("Authorization");
            if(authHeader == null || !authHeader.startsWith("Bearer ")) {
                return;
            }
            final String jwt = authHeader.substring(7);
            var storeToken = tokenRepository.findByAccessToken(jwt).orElse(null);
            if(storeToken != null) {
                storeToken.setExpired(true);
                storeToken.setRevoked(true);
                tokenRepository.save(storeToken);
                SecurityContextHolder.clearContext();
            }
        };
    }



}
