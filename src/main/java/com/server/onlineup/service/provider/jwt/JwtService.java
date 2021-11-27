package com.server.onlineup.service.provider.jwt;


import com.server.onlineup.common.constant.AuthenticationEnum;
import com.server.onlineup.common.exception.APIException;
import com.server.onlineup.common.response.BaseResponse;
import com.server.onlineup.security.principal.UserPrincipal;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {
    @Autowired
    private Environment env;

    private static final String secret = "nguyenthanhdat19022001@gmail.com";
    private static final long EXPIRE_TIME = 60 * 60 * 1000;
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class.getName());

    public String generateTokenFromEmail(String email) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, email);
    }

    // Generate token
    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, userPrincipal.getUsername());
    }

    // Get exp date of token
    public Date getExpirationDateFromToken(String token) {
        final Claims claims = getAllClaimsFromToken(token);
        return claims.getExpiration();
    }

    // Get username login
    public String getUserNameFromJwtToken(String token) {
        final Claims claims = getAllClaimsFromToken(token);
        return claims.getSubject();
    }

    // Bool Token expired
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    // Bool Validate token
    public boolean validateJwtToken(String authToken) {
        try {
            getAllClaimsFromToken(authToken);
            return true;
        } catch (SignatureException e) {
            throw new APIException(
                    BaseResponse.Builder()
                            .addMessage(AuthenticationEnum.INVALID_SIGNATURE_TOKEN)
                            .addErrorStatus(HttpStatus.UNAUTHORIZED)

            );
        } catch (MalformedJwtException e) {
            throw new APIException(
                    BaseResponse.Builder()
                            .addMessage(AuthenticationEnum.INVALID_TOKEN)
                            .addErrorStatus(HttpStatus.UNAUTHORIZED)
            );
        } catch (ExpiredJwtException e) {
            throw new APIException(
                    BaseResponse.Builder()
                            .addMessage(AuthenticationEnum.EXPIRED_TOKEN)
                            .addErrorStatus(HttpStatus.UNAUTHORIZED)
            );
        } catch (UnsupportedJwtException e) {
            throw new APIException(
                    BaseResponse.Builder()
                            .addMessage(AuthenticationEnum.UNSUPPORTED_TOKEN)
                            .addErrorStatus(HttpStatus.UNAUTHORIZED)
            );
        } catch (IllegalArgumentException e) {
            throw new APIException(
                    BaseResponse.Builder().addMessage(AuthenticationEnum.CLAIM_EMPTY_TOKEN)
            );
        }
    }

    // Helper function
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
    }

    private Key getSigningKey() {
        byte[] keyBytes = this.secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
