package com.workwise.springjwt.payload.response;

import lombok.Data;

import java.util.Date;
import java.util.List;
@Data
public class JwtResponse {
  private String token;
  private String type = "Bearer";
  private String refreshToken;
  private Long id;
  private String username;
  private String email;
  private List<String> roles;

  private Date expiration;

  public JwtResponse(String accessToken, String refreshToken, Long id, String username, String email, List<String> roles, Date expiration) {
    this.token = accessToken;
    this.refreshToken = refreshToken;
    this.id = id;
    this.username = username;
    this.email = email;
    this.roles = roles;
    this.expiration = expiration;
  }
}
