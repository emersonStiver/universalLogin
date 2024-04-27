package com.unisalle.universalLogin.services;

import com.unisalle.universalLogin.dtos.DeleteUserResult;
import com.unisalle.universalLogin.dtos.UserEntityDTO;

import java.util.List;

public interface UserManagementService {
    UserEntityDTO findUser(String userId);
    List<UserEntityDTO> findUsers(List<String> userIds);

    UserEntityDTO createUser(UserEntityDTO user);

    UserEntityDTO updateUser(String userId, UserEntityDTO user);

    DeleteUserResult deleteUser(String userId);

    boolean changePassword(String userId, String oldPassword, String newPassword);

    boolean userExists(String userId);
}

