package com.unisalle.universalLogin.exceptions;

import com.unisalle.universalLogin.exceptions.DBActionException;
import com.unisalle.universalLogin.dtos.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<UserNotFoundErrorResponse> throwException(UsernameNotFoundException exception) {
        System.out.println("Executing user not found exception");
        var errorMsg = UserNotFoundErrorResponse
                .builder()
                .success(false)
                .userId(exception.getMessage())
                .errorMessage("User was not found")
                .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorMsg);
    }
    @ExceptionHandler(DBActionException.class)
    public ResponseEntity<ErrorResponse> throwException(DBActionException exception){
        ErrorResponse errorResponse  = ErrorResponse.builder()
                .success(false)
                .message(exception.getMessage())
                .details(exception.getUserEntityDTO().toString())
                .build();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }
}
