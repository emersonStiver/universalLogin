package com.unisalle.universalLogin.exceptions;

import com.unisalle.universalLogin.dtos.UserEntityDTO;

public class DBActionException extends RuntimeException{
    private final UserEntityDTO entity;
    public DBActionException(UserEntityDTO user, String message){
        super(message);
        this.entity = user;
    }
    public UserEntityDTO getUserEntityDTO(){
        return entity;
    }
}
