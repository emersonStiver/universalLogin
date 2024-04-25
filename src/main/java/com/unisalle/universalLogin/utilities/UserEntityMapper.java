package com.unisalle.universalLogin.utilities;

import com.unisalle.universalLogin.dtos.UserEntityDTO;
import com.unisalle.universalLogin.entities.UserEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class UserEntityMapper {
    public UserEntity toUserEntity(UserEntityDTO user) {
        return UserEntity.builder()
                .identification(user.getIdentification())
                .email(user.getEmail())
                .isAccountExpired(false)
                .isAccountEnabled(true)
                .isAccountLocked(false)
                .isCredentialsExpired(false)
                .password(user.getPassword())
                .authorities(StringUtils.collectionToCommaDelimitedString(user.getRole()))
                .firstName(user.getFirstname())
                .lastName(user.getLastname())
                .build();
    }
    // Helper method to map UserEntity to UserRequest
    public UserEntityDTO toDto(UserEntity userEntity) {
        return UserEntityDTO.builder()
                .identification(userEntity.getIdentification())
                .firstname(userEntity.getFirstName())
                .lastname(userEntity.getLastName())
                .email(userEntity.getEmail())
                .role(StringUtils.commaDelimitedListToSet(userEntity.getAuthorities()))
                .build();
    }
}
