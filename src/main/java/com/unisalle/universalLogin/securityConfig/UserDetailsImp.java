package com.unisalle.universalLogin.securityConfig;

import com.unisalle.universalLogin.entities.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
public class UserDetailsImp implements UserDetails {
    private UserEntity userEntity;
    public UserDetailsImp(UserEntity userEntity){
        this.userEntity = userEntity;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities( ){
        Set<String> authorities = StringUtils.commaDelimitedListToSet(userEntity.getAuthorities());
        return authorities.stream()
                .map(String::trim)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword( ){
        return userEntity.getPassword();
    }

    @Override
    public String getUsername( ){
        return userEntity.getUserId();
    }

    @Override
    public boolean isAccountNonExpired( ){
        return userEntity.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked( ){
        return userEntity.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired( ){
        return userEntity.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled( ){
        return userEntity.isAccountEnabled();
    }
}
