package com.unisalle.universalLogin.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordChangeRequest implements Serializable{
    private String oldPassword;
    private String newPassword;
}
