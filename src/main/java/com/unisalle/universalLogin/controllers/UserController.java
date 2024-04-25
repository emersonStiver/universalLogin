package com.unisalle.universalLogin.controllers;

import com.unisalle.universalLogin.dtos.UserEntityDTO;
import com.unisalle.universalLogin.services.UserManagementService;
import com.unisalle.universalLogin.dtos.DeleteUserResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/users")
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

    @GetMapping("/{id}")
    public ResponseEntity<UserEntityDTO> getUser(@PathVariable("id") long id) {
        Optional<UserEntityDTO> user = Optional.ofNullable(userManagementService.findUser(id));
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserEntityDTO> createUser(@RequestBody UserEntityDTO user) {
        UserEntityDTO createdUser = userManagementService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserEntityDTO> updateUser(@PathVariable("id") long id, @RequestBody UserEntityDTO user) {
        user.setId(id); // Ensure the ID is set from the path variable
        UserEntityDTO updatedUser = userManagementService.updateUser(user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity  deleteUser(@PathVariable("username") String username) {
        DeleteUserResult result = userManagementService.deleteUser(username);
        if (result.isSuccess()) {
            return ResponseEntity.ok().body(result.getMessage());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result.getMessage());
        }
    }

    @PatchMapping("/{email}/password")
    public ResponseEntity<String> changePassword(@PathVariable("email") String email,
                                                 @RequestParam("oldPassword") String oldPassword,
                                                 @RequestParam("newPassword") String newPassword) {
        try {
            boolean passwordChanged = userManagementService.changePassword(email, oldPassword, newPassword);
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

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> userExists(@PathVariable("id") long id) {
        boolean exists = userManagementService.userExists(id);
        return ResponseEntity.ok().body(exists);
    }
}


