package com.unisalle.universalLogin.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name ="identification", unique = true, nullable = false)
    private long identification;
    @Column(name ="first_name", nullable = false)
    private String firstName;
    @Column(name ="last_name", nullable = false)
    private String lastName;
    @Column(name ="email", unique = true, nullable = false)
    private String email;
    @Column(name ="password", nullable = false)
    private String password;
    private String authorities;
    @Column(name ="creation_date", nullable = false)
    private LocalDate creationDate;
    @Column(name ="updated_at")
    private LocalDateTime lastUpdate;
    @Column(name="is_expired", nullable = false)
    private boolean isAccountExpired;
    @Column(name="is_credentials_expired", nullable = false)
    private boolean isCredentialsExpired;
    @Column(name="is_locked", nullable = false)
    private boolean isAccountLocked;
    @Column(name="is_enabled", nullable = false)
    private boolean isAccountEnabled;
}
