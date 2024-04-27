package com.unisalle.universalLogin.securityConfig;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@RequiredArgsConstructor
public enum Permission implements Serializable {
    USER_READ("user::read"),
    USER_UPDATE("user::update"),
    USER_CREATE("user::create"),
    USER_DELETE("user::delete"),
    ADMIN_READ("admin::read"),
    ADMIN_UPDATE("admin::update"),
    ADMIN_CREATE("admin::create"),
    ADMIN_DELETE("admin::delete"),
    MANAGER_READ("manager::read"),
    MANAGER_UPDATE("manager::update"),
    MANAGER_CREATE("manager::create"),
    MANAGER_DELETE("manager::delete");

    @Getter
    public final String permission;

}
