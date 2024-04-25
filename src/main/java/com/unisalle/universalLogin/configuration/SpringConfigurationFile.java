package com.unisalle.universalLogin.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unisalle.universalLogin.repositories.UserRepository;
import com.unisalle.universalLogin.securityConfig.UserDetailsImp;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.EnableTransactionManagement;
/*
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

 */

@Configuration
@ComponentScan(value = {"com.unisalle.universalLogin"})
@EnableJpaRepositories(value = {"com.unisalle.universalLogin"})
@EnableTransactionManagement
//@PropertySource(value = {"com.unisalle.universalLogin"})
//@EnableSwagger2
public class SpringConfigurationFile {
    @Bean
    public ObjectMapper mapper(){
        return new ObjectMapper();
    }
    /*
    @Bean
    public Docket api(){
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.unisalle.universalLogin"))
                .build();
    }

     */
    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository){
        return username -> userRepository
                .findByUsername(username)
                .map(UserDetailsImp::new)
                .orElseThrow(() -> new UsernameNotFoundException("Username " + username + " was not found"));
    }




}
