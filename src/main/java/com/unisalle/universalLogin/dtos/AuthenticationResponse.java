package com.unisalle.universalLogin.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import java.io.Serializable;

@Data
@Builder
public class AuthenticationResponse implements Serializable{
    @JsonProperty ("access_token")
    private String accessToken;
    @JsonProperty("refresh_token")
    private String refreshToken;

}
