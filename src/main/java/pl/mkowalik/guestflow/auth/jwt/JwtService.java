package pl.mkowalik.guestflow.auth.jwt;

import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Service
public class JwtService {
    
    @Value("${jwt.secret}")
    String secret;

    private SecretKey getSignedKey(){
        return new SecretKeySpec(secret.getBytes(), "HmacSHA256");
    }
    
    public String generateToken(Map<String, String> extraClaims, String userName, long expireInterval){
        return Jwts
            .builder()
            .claims().add(extraClaims)
            .and()
            .subject(userName)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + expireInterval))
            .signWith(getSignedKey())
            .compact();
    }

    public String getUserName(String token){
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    public boolean isTokenExpired(String token){
        Claims claims = extractAllClaims(token);
        return claims.getExpiration().before(new Date(System.currentTimeMillis()));
    }

    private Claims extractAllClaims(String token){
        return Jwts
            .parser()
            .verifyWith(getSignedKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}
