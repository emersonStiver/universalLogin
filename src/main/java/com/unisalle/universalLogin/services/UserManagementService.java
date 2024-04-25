package com.unisalle.universalLogin.services;

import com.unisalle.universalLogin.dtos.DeleteUserResult;
import com.unisalle.universalLogin.dtos.UserEntityDTO;

import java.util.List;

public interface UserManagementService {
    UserEntityDTO findUser(long id);
    List<UserEntityDTO> findUsers(List<Long> userIdentifications);

    UserEntityDTO createUser(UserEntityDTO user);

    UserEntityDTO updateUser(UserEntityDTO user);

    DeleteUserResult deleteUser(String username);

    boolean changePassword(String username, String oldPassword, String newPassword);

    boolean userExists(long id);
}

