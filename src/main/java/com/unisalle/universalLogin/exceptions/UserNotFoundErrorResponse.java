package com.unisalle.universalLogin.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserNotFoundErrorResponse {
    private boolean success;
    private String userId;
    private String errorMessage;
}
