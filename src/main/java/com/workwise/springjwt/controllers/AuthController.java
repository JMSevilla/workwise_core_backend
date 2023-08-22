package com.workwise.springjwt.controllers;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.example.workwise.workwiseframework.payload.request.LoginRequest;
import com.workwise.springjwt.exception.TokenRefreshException;
import com.workwise.springjwt.models.RefreshToken;
import com.workwise.springjwt.payload.request.SignupRequest;
import com.workwise.springjwt.payload.request.TokenRefreshRequest;
import com.workwise.springjwt.payload.response.TokenRefreshResponse;
import com.workwise.springjwt.service.AccountService;
import com.workwise.springjwt.service.RefreshTokenService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.workwise.springjwt.payload.response.JwtResponse;
import com.workwise.springjwt.payload.response.MessageResponse;
import com.workwise.springjwt.repository.RoleRepository;
import com.workwise.springjwt.security.jwt.JwtUtils;
import com.workwise.springjwt.security.services.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;
  @Value("${ww_be_v2.app.jwtExpirationMs}")
  private int jwtExpirationMs;
  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;
  @Autowired
  private AccountService accountService;
  @Autowired
  private RefreshTokenService refreshTokenService;
  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    String jwt = jwtUtils.generateJwtToken(userDetails);
    List<String> roles = userDetails.getAuthorities().stream()
            .map(item -> item.getAuthority())
            .collect(Collectors.toList());
    Date expirationDate = new Date(System.currentTimeMillis() + jwtExpirationMs);
    RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
    return ResponseEntity.ok(new JwtResponse(jwt,
            refreshToken.getToken(),
            userDetails.getId(),
            userDetails.getUsername(),
            userDetails.getEmail(),
            roles, expirationDate));
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest request) {
    if(accountService.accountCreation(request) == 301){
      return ResponseEntity
              .badRequest()
              .body(new MessageResponse("Error: Username is already taken!"));
    }
    else if(accountService.accountCreation(request) == 302){
      return ResponseEntity
              .badRequest()
              .body(new MessageResponse("Error: Email is already in use!"));
    }
    else {
      return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
  }
  @PostMapping("/refresh-token")
  public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request){
    String requestRefreshToken = request.getRefreshToken();

    return refreshTokenService.findByToken(requestRefreshToken)
            .map(refreshTokenService::verifyExpiration)
            .map(RefreshToken::getUser)
            .map(user -> {
              String token = jwtUtils.generateTokenFromUsername(user.getUsername());
              return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
            }).orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                    "Refresh token is not in database!"));
  }
}
