package com.unisalle.universalLogin.repositories;

import com.unisalle.universalLogin.entities.Token;
import jakarta.websocket.server.PathParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    //List<Token> findAllValidTokenByUser(@Param ("id") Long id);

    Optional<Token> findByAccessToken(String jwt);
}
