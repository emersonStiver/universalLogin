package com.unisalle.universalLogin.securityConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
@Component
public class JwtUtils {
    @Value ("${myapp.security.jwt.secretKey}")
    private String secretKey;
    @Value("${myapp.security.jwt.expirationTime}")
    private long expiration;
    @Value("${myapp.security.jwt.refreshExpiration}")
    private long refreshExpiration;

    //BUILD JWT TOKEN
    public String generateJwtToken(UserDetails user, Map<String, Object> claims){
        return buildToken(user, claims, expiration);
    }
    public String generateJwtToken(UserDetails user){
        return buildToken(user, new HashMap<>(), expiration);
    }
    public String generateJwtRefreshToken(UserDetails user){
        return buildToken(user, new HashMap<>(), refreshExpiration);
    }
    //Key is mixed with the header and payload in a secure way, then, all of it is hashed using HS256
    public String buildToken(UserDetails userDetails, Map<String, Object> extraClaims, long expirationTime){
        return Jwts.builder()
                .addClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(keyGenerator(), SignatureAlgorithm.HS256)
                .compact();
    }



    //METHODS TO EXTRACT INFORMATION FROM THE TOKEN

    private Claims extractAllClaims(String token){
        JwtParser parser = Jwts.parserBuilder().setSigningKey(keyGenerator()).build();
        parser.parseClaimsJws(token).getHeader();
        //parser.parseClaimsJws(token).getSignature();
        Claims claims = parser.parseClaimsJws(token).getBody();
        return claims;
    }

    private <T> T extractClaim(String token, Function<Claims, T> function){
        Claims claims = extractAllClaims(token);
        return function.apply(claims);
    }

    private Date extractExpirationDate(String token){
        return extractClaim(token, (Claims claims) -> claims.getExpiration());
    }
    private boolean isTokenExpired(String token){
        return extractClaim(token, (Claims claims) -> claims.getExpiration()).before(new Date());
    }
    public String extractSubject(String token){
        return extractClaim(token, (Claims claims) -> claims.getSubject());
    }
    public boolean isTokenValid(String token, UserDetails userDetails){
        String user = extractSubject(token);
        return user.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }


    //Creates a byte array from a string key and formats it to use it for
    public Key keyGenerator(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /*
    //VALIDAT EL TOKEN DE ACCESO, cuando un usuario se valide con nosotros
    //debemos validar que ese token sea correto
    public boolean isTokenValid(String token){
        try{
            Jwts.parserBuilder()//hace lo contrario a generar token, este lee el encriptado de generate token
                    .setSigningKey(getSignatureKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return true;
        }catch(Exception e){
            log.error("Token invalido, error: ".concat(e.getMessage()));
            return false;
        }
    }
     */

}
