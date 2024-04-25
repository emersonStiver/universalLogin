package com.unisalle.universalLogin.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResponseWrapper<T> {
    private String status;
    private String message;
    private T data;

}
