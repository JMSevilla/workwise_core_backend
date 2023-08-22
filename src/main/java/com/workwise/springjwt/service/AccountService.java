package com.workwise.springjwt.service;

import com.workwise.springjwt.payload.request.LoginRequest;
import com.workwise.springjwt.payload.request.SignupRequest;
import com.workwise.springjwt.payload.request.TokenRefreshRequest;
import org.springframework.http.ResponseEntity;

public interface AccountService {
    public Integer accountCreation(SignupRequest signupRequest);
}
