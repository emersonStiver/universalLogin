package com.unisalle.universalLogin.controllers;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// -------------------------- IN DEVELOPMENT --------------------------
@RestController
@RequestMapping("/api/v1/organizations")
@Tag(name = "Organizations")
@PreAuthorize("hasRole('Organization')")
public class OrganizationController {
    @Operation (
            description = "Get endpoint for manager",
            summary = "This is a summary for management get endpoint",
            responses = {
                    @ApiResponse (
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    )
            }

    )
    @GetMapping("/find")
    @PreAuthorize("hasAuthority('organization:read')")
    public String getOrganization(){
        return "Hola soy una organization";
    }
    @GetMapping
    @Hidden
    public String testEndpoint(){
        return "hiii";
    }
}
