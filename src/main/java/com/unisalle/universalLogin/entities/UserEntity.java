package com.unisalle.universalLogin.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UserEntity implements Serializable, Cloneable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long dbId;
    @Column(name = "user_id", nullable = false, updatable = false, unique = true)
    private String userId;
    @Column(name ="legal_id", unique = true, nullable = false)
    private String nationalId;
    @Column(name ="first_name", nullable = false)
    private String firstName;
    @Column(name ="last_name", nullable = false)
    private String lastName;
    @Column(name ="email", unique = true, nullable = false)
    private String email;
    @Column(name ="password", nullable = false)
    private String password;
    private String department;
    private String city;
    private String authorities;
    @Column(name ="creation_date", nullable = false)
    private LocalDate creationDate;
    @Column(name ="updated_at")
    private LocalDateTime lastUpdate;
    @Column(name="is_expired", nullable = false)
    private boolean isAccountNonExpired;
    @Column(name="is_credentials_expired", nullable = false)
    private boolean isCredentialsNonExpired;
    @Column(name="is_locked", nullable = false)
    private boolean isAccountNonLocked;
    @Column(name="is_enabled", nullable = false)
    private boolean isAccountEnabled;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Token> tokens;

    @Override
    public boolean equals(Object o) {
        //checks if both point to the same memory address
        if(this == o){
            return true;
        }
        //checks if o is null or if their classes are NOT different
        if(o == null || getClass() != o.getClass()){
            return false;
        }
        UserEntity other = (UserEntity) o;
        return  dbId == other.dbId &&
                Objects.equals(userId, other.userId) &&
                Objects.equals(nationalId, other.nationalId) &&
                Objects.equals(firstName, other.firstName) &&
                Objects.equals(lastName, other.lastName) &&
                Objects.equals(email, other.email) &&
                Objects.equals(password, other.password) &&
                Objects.equals(department, other.department) &&
                Objects.equals(city, other.city) &&
                Objects.equals(authorities, other.authorities) &&
                Objects.equals(creationDate, other.creationDate) &&
                Objects.equals(lastUpdate, other.lastUpdate) &&
                isAccountNonExpired == other.isAccountNonExpired &&
                isCredentialsNonExpired == other.isCredentialsNonExpired &&
                isAccountNonLocked == other.isAccountNonLocked &&
                isAccountEnabled == other.isAccountEnabled;
    }

    @Override
    public UserEntity clone( ){
        try {
            UserEntity clone = (UserEntity) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
