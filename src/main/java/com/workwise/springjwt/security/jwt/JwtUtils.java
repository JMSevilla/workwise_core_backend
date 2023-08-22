package com.workwise.springjwt.security.jwt;

import java.security.Key;
import java.util.Date;

import com.workwise.springjwt.security.services.SecureKeyGenerator;
import com.workwise.springjwt.security.services.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {
  private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

  @Value("${ww_be_v2.app.jwtSecret}")
  private String jwtSecret;

  @Value("${ww_be_v2.app.jwtExpirationMs}")
  private int jwtExpirationMs;

  @Autowired
  private SecureKeyGenerator secureKeyGenerator;

  public String generateJwtToken(UserDetailsImpl userPrincipal) {
    return generateTokenFromUsername(userPrincipal.getUsername());
  }

  public String generateTokenFromUsername(String username){
    String jwtSecretGenerated = secureKeyGenerator.generateHmacKey(512, "");
    return Jwts.builder().setSubject(username).setIssuedAt(new Date())
            .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)).signWith(SignatureAlgorithm.HS512, jwtSecretGenerated)
            .compact();
  }

  public String getUserNameFromJwtToken(String token) {
    String jwtSecretGenerated = secureKeyGenerator.generateHmacKey(512, "");
    return Jwts.parser().setSigningKey(jwtSecretGenerated).parseClaimsJwt(token).getBody().getSubject();
  }

  public boolean validateJwtToken(String authToken) {
    try {
      String jwtSecretGenerated = secureKeyGenerator.generateHmacKey(512, "");
      Jwts.parserBuilder().setSigningKey(jwtSecretGenerated).build().parse(authToken);
      return true;
    } catch (MalformedJwtException e) {
      logger.error("Invalid JWT token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      logger.error("JWT token is expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      logger.error("JWT token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      logger.error("JWT claims string is empty: {}", e.getMessage());
    }

    return false;
  }
}
