package com.unisalle.universalLogin.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResponseWrapper<T> implements Serializable{
    private String status;
    private String message;
    private T data;

}
