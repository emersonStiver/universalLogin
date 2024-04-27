package com.unisalle.universalLogin.controllers;

import com.unisalle.universalLogin.dtos.PasswordChangeRequest;
import com.unisalle.universalLogin.dtos.UserEntityDTO;
import com.unisalle.universalLogin.services.UserManagementService;
import com.unisalle.universalLogin.dtos.DeleteUserResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping(value = "/api/v1/users", produces = "application/json")
public class UserController {
    private UserManagementService userManagementService;
    private Validator validator;
    private Logger log = LoggerFactory.getLogger(UserController.class);
    public UserController(UserManagementService userManagementService, Validator validator){
        this.validator = validator;
        this.userManagementService = userManagementService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(validator);
    }

    @GetMapping(value = "/user-info")
    @PreAuthorize("hasAuthority('user::read')")
    public ResponseEntity<UserEntityDTO> getUserInfo(@AuthenticationPrincipal String userId) {
        UserEntityDTO user = userManagementService.findUser(userId);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/create-user")
    public ResponseEntity<UserEntityDTO> createUser(@RequestBody UserEntityDTO user) {
        UserEntityDTO createdUser = userManagementService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/edit")
    @PreAuthorize("hasAuthority('user::update')")
    public ResponseEntity<UserEntityDTO> editUser(@AuthenticationPrincipal String userId, @RequestBody UserEntityDTO user) {
        UserEntityDTO updatedUser = userManagementService.updateUser(userId, user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping(value = "/delete-user")
    @PreAuthorize("hasAuthority('user::delete')")
    public ResponseEntity<DeleteUserResult> deleteUser(@AuthenticationPrincipal String userId) {
        System.out.println("iniciando eliminacion");
        DeleteUserResult result = userManagementService.deleteUser(userId);
        if (result.isSuccess()) {
            return ResponseEntity.ok().body(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }

    @PatchMapping(value = "/password")
    @PreAuthorize("hasAuthority('user::update')")
    public ResponseEntity<String> changePassword(Authentication auth, @RequestBody PasswordChangeRequest request) {
        String userId = (String) auth.getPrincipal();
        try {
            boolean passwordChanged = userManagementService.changePassword(userId, request.getOldPassword(), request.getNewPassword());
            if (passwordChanged) {
                return ResponseEntity.ok().body("Password changed successfully");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to change password");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/exists/{email}")
    public ResponseEntity<Map<String, Object> > userExists(@PathVariable("email") String email) {
        boolean exists = userManagementService.userExists(email);
        Map<String, Object> response = new HashMap<>();
        response.put("exists", exists);
        response.put("emaiL", email);
        return ResponseEntity.ok().body(response);
    }
}


