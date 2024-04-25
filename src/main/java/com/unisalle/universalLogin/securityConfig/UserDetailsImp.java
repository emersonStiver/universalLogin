package com.unisalle.universalLogin.securityConfig;

import com.unisalle.universalLogin.entities.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;
public class UserDetailsImp implements UserDetails {
    private UserEntity userEntity;
    public UserDetailsImp(UserEntity userEntity){
        this.userEntity = userEntity;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities( ){
        return userEntity.getAuthorities().stream()
                .flatMap( simpleAuthorizationSet -> simpleAuthorizationSet.getAuthorities().stream())
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword( ){
        return userEntity.getPassword();
    }

    @Override
    public String getUsername( ){
        return userEntity.getUsername();
    }

    @Override
    public boolean isAccountNonExpired( ){
        return userEntity.isAccountExpired();
    }

    @Override
    public boolean isAccountNonLocked( ){
        return userEntity.isAccountLocked();
    }

    @Override
    public boolean isCredentialsNonExpired( ){
        return userEntity.isCredentialsExpired();
    }

    @Override
    public boolean isEnabled( ){
        return userEntity.isAccountEnabled();
    }
}
