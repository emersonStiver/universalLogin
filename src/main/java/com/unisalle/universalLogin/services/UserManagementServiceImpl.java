package com.unisalle.universalLogin.services;

import com.unisalle.universalLogin.dtos.DeleteUserResult;
import com.unisalle.universalLogin.dtos.UserEntityDTO;
import com.unisalle.universalLogin.entities.UserEntity;
import com.unisalle.universalLogin.repositories.UserRepository;
import com.unisalle.universalLogin.utilities.UserEntityMapper;
import lombok.extern.slf4j.Slf4j;
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
    private UserManagementServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserEntityMapper userEntityMapper){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userEntityMapper = userEntityMapper;
    }
    @Override
    public UserEntityDTO findUser(long id) {
        UserEntity userEntity = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User with id: " + id + " was not found"));
        return userEntityMapper.toDto(userEntity);
    }

    @Override
    public List<UserEntityDTO> findUsers(List<Long> userIdentifications){
        List<UserEntity> users = userRepository
                .findAllUsersByIds(userIdentifications)
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
            throw new RuntimeException("Failed to create user: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public UserEntityDTO updateUser(UserEntityDTO user) {
        // Define a function to verify and update string values
        BiFunction<String, String, String> verifyUpdatedString = (oldValue, newValue) -> {
            return (newValue != null && !newValue.isEmpty()) ? newValue : oldValue;
        };

        try {
            // Fetch the user entity from the repository
            UserEntity userEntity = userRepository.findByEmail(user.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User " + user.getEmail() + " was not found"));

            // Update user entity properties
            userEntity.setFirstName(verifyUpdatedString.apply(userEntity.getFirstName(), user.getFirstname()));
            userEntity.setLastName(verifyUpdatedString.apply(userEntity.getLastName(), user.getLastname()));
            userEntity.setEmail(verifyUpdatedString.apply(userEntity.getEmail(), user.getEmail()));
            userEntity.setLastUpdate(LocalDateTime.now());

            // Save the updated user entity
            UserEntity updatedUserEntity = userRepository.save(userEntity);

            // Check if the update was successful
            if (updatedUserEntity == userEntity) {
                // Row was not updated
                throw new RuntimeException("User was not updated: " + user.getEmail());
            }

            // Return the updated user request
            return user;
        } catch (Exception e) {
            // Throw a runtime exception to trigger transaction rollback
            throw new RuntimeException("Failed to update user: " + user.getEmail(), e);
        }
    }

    @Override
    public DeleteUserResult deleteUser(String username) {
        DeleteUserResult result = new DeleteUserResult();

        try {
            // Retrieve the user from the database
            UserEntity user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found"));

            // Delete the user
            int row = userRepository.deleteUserById(user.getId());
            if(row == 0){
                throw new RuntimeException("User " + username + " was not deleted");
            }

            result.setSuccess(true);
            result.setMessage("User deleted successfully");
            result.setUser(user); // Optionally, include the deleted user in the result
        } catch (UsernameNotFoundException e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("An error occurred while deleting the user");
            // Optionally, log the exception or additional details
        }
        return result;
    }

    @Override
    public boolean changePassword(String email, String oldPassword, String newPassword) {
        // Find the user by email
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with email: " + email + " not found"));

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
            throw new RuntimeException("Failed to update password for user: " + email);
        }

        return true;
    }

    @Override
    public boolean userExists(long id) {
        return userRepository.findById(id).isPresent();
    }

}
