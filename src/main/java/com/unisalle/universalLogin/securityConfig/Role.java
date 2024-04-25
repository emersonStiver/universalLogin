package com.unisalle.universalLogin.securityConfig;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import static com.unisalle.universalLogin.securityConfig.Permission.*;
public enum Role {
    USER(Collections.emptySet()),
    ADMIN(
            Set.of(
                    ADMIN_READ,
                    ADMIN_UPDATE,
                    ADMIN_DELETE,
                    ADMIN_CREATE,
                    MANAGER_READ,
                    MANAGER_UPDATE,
                    MANAGER_DELETE,
                    MANAGER_CREATE
            )
    ),
    MANAGER(
            Set.of(
                    MANAGER_READ,
                    MANAGER_UPDATE,
                    MANAGER_DELETE,
                    MANAGER_CREATE
            )
    )
    ;
    Role(Set<Permission> permisssionSet){
        this.permissions = permisssionSet;
    }

    @Getter
    private Set<Permission> permissions;

    public Set<SimpleGrantedAuthority> getAuthorities(){
        return getPermissions().stream()
                .map(Permission::getPermission)
                .map(permission -> new SimpleGrantedAuthority("Role_"+permission))
                .collect(Collectors.toSet());
    }

}
