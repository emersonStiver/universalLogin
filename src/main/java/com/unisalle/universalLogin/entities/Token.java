package com.unisalle.universalLogin.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "tokens")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Token implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "access_token",unique = true, length = 500)
    private String accessToken;
    @Column(name = "refresh_token",unique = true)
    private String refreshToken;
    private TokenType tokenType;
    private boolean isRevoked;
    private boolean isExpired;
    @ManyToOne(targetEntity = UserEntity.class)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private UserEntity user;
}
