package com.unisalle.universalLogin.dtos;

import com.unisalle.universalLogin.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeleteUserResult implements Serializable {
    private boolean success;
    private String message;
}
