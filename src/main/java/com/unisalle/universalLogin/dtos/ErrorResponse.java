package com.unisalle.universalLogin.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse implements Serializable {
    private boolean success;
    private String message;
    private String details;
}
