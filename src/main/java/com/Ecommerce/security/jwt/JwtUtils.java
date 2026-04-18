package com.Ecommerce.security.jwt;

import com.Ecommerce.security.services.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${spring.app.jwtExpirationMS}")
    private Integer jwtExpirationMS;

    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;

    @Value("${spring.app.jwtCookie}")
    private String jwtCookie;

    public String getJwtTokenFromHeader(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        logger.debug("Authorization Header: {}",bearerToken);
        if(bearerToken!=null && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }

    public String getJwtFromCookie(HttpServletRequest request){
        Cookie cookie = WebUtils.getCookie(request,jwtCookie);
        if(cookie!=null){
            return cookie.getValue();
        }
        else {
            return null;
        } 
    }

    public ResponseCookie generateJwtCookie(UserDetailsImpl userDetails){
        String jwtToken = generateJwtTokenFromUsername(userDetails.getUsername());
        return ResponseCookie.from(jwtCookie, jwtToken).path("/api").maxAge(24*60*60).httpOnly(true).build();
    }

    public ResponseCookie getCleanJwtCookie(){
        ResponseCookie cookie = ResponseCookie.from(jwtCookie, null).maxAge(0).httpOnly(true).path("/api").build();
        return cookie;
    }

    public String generateJwtTokenFromUsername(String username){
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + jwtExpirationMS))
                .signWith(key())
                .compact();
    }

    public String getUserNameFromJwtToken(String token){
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    private Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(jwtSecret));
    }


    public boolean validateJwtToken(String authToken){
        try{
            System.out.println("Validate: ");
            Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(authToken);
            return true;
        }
        catch(MalformedJwtException e){
            logger.error("Invalid JWT Token: {}",e.getMessage());
        }
        catch(ExpiredJwtException e){
            logger.error("JWT Token is expired: {}",e.getMessage());
        }
        catch(UnsupportedJwtException e){
            logger.error("JWT Token is Unsupported: {}",e.getMessage());
        }
        catch(IllegalArgumentException e){
            logger.error("JWT claims String is empty: {}",e.getMessage());
        }
        return false;
    }
}
