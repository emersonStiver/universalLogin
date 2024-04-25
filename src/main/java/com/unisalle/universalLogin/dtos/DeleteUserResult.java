package com.unisalle.universalLogin.dtos;

import com.unisalle.universalLogin.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeleteUserResult {
    private boolean success;
    private String message;
    private UserEntity user; // Optionally include the deleted user
}
