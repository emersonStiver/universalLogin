package com.unisalle.universalLogin.services;

import com.unisalle.universalLogin.dtos.DeleteUserResult;
import com.unisalle.universalLogin.dtos.UserEntityDTO;
import com.unisalle.universalLogin.entities.UserEntity;
import com.unisalle.universalLogin.exceptions.DBActionException;
import com.unisalle.universalLogin.repositories.UserRepository;
import com.unisalle.universalLogin.utilities.UserEntityMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserManagementServiceImpl implements UserManagementService {
    private UserEntityMapper userEntityMapper;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    public UserManagementServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserEntityMapper userEntityMapper){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userEntityMapper = userEntityMapper;
    }
    @Cacheable (value = "user", key = "#userId")
    @Override
    public UserEntityDTO findUser(String userId) {
        UserEntity userEntity = userRepository
                .findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException(userId));
        return userEntityMapper.toDto(userEntity);
    }

    @Override
    public List<UserEntityDTO> findUsers(List<String> userIds){
        List<UserEntity> users = userRepository
                .findAllUsersByIds(userIds)
                .orElseThrow(() -> new RuntimeException("Users were not found"));
        return users.stream().map(userEntity -> userEntityMapper.toDto(userEntity)).collect(Collectors.toList());
    }

    @Override
    public UserEntityDTO createUser(UserEntityDTO user) {
        try {
            // Map UserRequest to UserEntity and save it
            UserEntity savedUserEntity = userRepository.save(userEntityMapper.toUserEntity(user));

            // Map the saved UserEntity back to UserRequest and return it
            return userEntityMapper.toDto(savedUserEntity);
        } catch (Exception e) {
            // Handle any exceptions and log or rethrow as needed
            throw new DBActionException(user ,e.getMessage());
        }
    }
    @CachePut (cacheNames = "user", key = "#userId")
    @Override
    @Transactional
    public UserEntityDTO updateUser(String userId, UserEntityDTO user) {
        // Define a function to verify and update string values
        BiFunction<String, String, String> verifyUpdatedString = (oldValue, newValue) -> {
            return (newValue != null && !newValue.isEmpty()) ? newValue : oldValue;
        };

        try {
            // Fetch the user entity from the repository
            UserEntity userEntity = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("User " + user.getEmail() + " was not found"));
            // Update user entity properties
            userEntity.setFirstName(verifyUpdatedString.apply(userEntity.getFirstName(), user.getFirstName()));
            userEntity.setLastName(verifyUpdatedString.apply(userEntity.getLastName(), user.getLastName()));
            userEntity.setEmail(verifyUpdatedString.apply(userEntity.getEmail(), user.getEmail()));
            userEntity.setLastUpdate(LocalDateTime.now());

            // Save the updated user entity
            UserEntity clonedEntity = userEntity.clone();
            UserEntity updatedUserEntity = userRepository.save(clonedEntity);

            // Check if the update was successful
            if (updatedUserEntity == clonedEntity) {
                // Row was not updated
                throw new DBActionException(user ,"ACTUALIZACION_SIN_CAMBIOS_DATOS_IDENTICOS");
            }
            // Return the updated user request
            return user;
        } catch (Exception e) {
            // Handle any exceptions and log or rethrow as needed
            throw new DBActionException(user ,e.getMessage());
        }
    }
    @CacheEvict (cacheNames = "user", key = "#userId")
    @Transactional
    @Override
    public DeleteUserResult deleteUser(String userId) {
        DeleteUserResult result = new DeleteUserResult();
        try {
            // Delete the user
            int row = userRepository.deleteByUserId(userId);
            if(row == 0){
                throw new RuntimeException(userId);
            }
            result.setSuccess(true);
            result.setMessage(row + " User deleted successfully");
        } catch (RuntimeException e) {
            result.setSuccess(false);
            result.setMessage("An error occurred while deleting the user: " + e.getMessage());
        }
        return result;
    }
    @CachePut(value = "user", key = "#userId")
    @Override
    public boolean changePassword(String userId, String oldPassword, String newPassword) {
        // Find the user by email
        UserEntity user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException(userId));

        // Check if the old password matches
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Old password doesn't match.");
        }

        // Set the new password
        user.setPassword(passwordEncoder.encode(newPassword));

        // Save the updated user
        UserEntity updatedUser = userRepository.save(user);

        // Check if user update was successful
        if (!updatedUser.getPassword().equals(user.getPassword())) {
            // Rollback the password change if update failed
            throw new RuntimeException("Failed to update password for user: " + userId);
        }

        return true;
    }

    @Override
    public boolean userExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

}
