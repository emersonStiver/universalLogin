package com.unisalle.universalLogin.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntityDTO implements Serializable {
    @NotNull
    private String nationalId;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String department;

    @NotBlank
    private String city;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    private Set<String> role;
}
