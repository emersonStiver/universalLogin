package com.unisalle.universalLogin.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unisalle.universalLogin.repositories.UserRepository;
import com.unisalle.universalLogin.securityConfig.UserDetailsImp;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
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
@EnableCaching
public class SpringConfigurationFile {
    @Bean
    public ObjectMapper mapper(){
        return new ObjectMapper();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository){
        return id -> {
            if(id.contains("@")){
                return userRepository
                        .findByEmail(id)
                        .map(UserDetailsImp::new)
                        .orElseThrow(() -> new UsernameNotFoundException("Username with email" + id + " was not found"));
            }else{
                return userRepository
                        .findByUserId(id)
                        .map(UserDetailsImp::new)
                        .orElseThrow(() -> new UsernameNotFoundException("Username with uuid" + id + " was not found"));
            }
        };
    }





}
