package com.unisalle.universalLogin.utilities;

import com.unisalle.universalLogin.dtos.UserEntityDTO;
import com.unisalle.universalLogin.entities.UserEntity;
import com.unisalle.universalLogin.securityConfig.Role;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class UserEntityMapper {
    private PasswordEncoder passwordEncoder;
    private UserEntityMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    public UserEntity toUserEntity(UserEntityDTO user) {
        user.setRole(Set.of(StringUtils
                .collectionToCommaDelimitedString(Role.USER.getAuthorities()
                        .stream()
                        .map(SimpleGrantedAuthority::getAuthority)
                        .collect(Collectors.toSet()))));
        System.out.println(user.getRole());
        user.getRole().forEach(e -> System.out.println(e.toUpperCase()));
        return UserEntity.builder()
                .userId(UUID.randomUUID().toString())
                .nationalId(user.getNationalId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .password(passwordEncoder.encode(user.getPassword()))
                .department(user.getDepartment())
                .city(user.getCity())
                .creationDate(LocalDate.now())
                .isAccountNonExpired(true)
                .isAccountEnabled(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .authorities(StringUtils.collectionToCommaDelimitedString(user.getRole()))
                .build();
    }
    // Helper method to map UserEntity to UserRequest
    public UserEntityDTO toDto(UserEntity userEntity) {
        return UserEntityDTO.builder()
                .nationalId(userEntity.getNationalId())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .email(userEntity.getEmail())
                .city(userEntity.getCity())
                .department(userEntity.getDepartment())
                .password("[PROTECTED]")
                .role(StringUtils.commaDelimitedListToSet(userEntity.getAuthorities()))
                .build();
    }
}
