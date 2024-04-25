package com.unisalle.universalLogin.repositories;

import com.unisalle.universalLogin.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String userEmail);

    @Query(value = "select * from users s where s.identification in :ids", nativeQuery = true)
    Optional<List<UserEntity>> findAllUsersByIds(@Param("ids") List<Long> ids);
    @Modifying
    @Query(value = "delete from users u where u.id = id", nativeQuery = true)
    int deleteUserById(@Param("id") long id);

}
