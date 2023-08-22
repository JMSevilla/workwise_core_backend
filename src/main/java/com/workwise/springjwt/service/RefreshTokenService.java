package com.workwise.springjwt.service;

import com.workwise.springjwt.models.RefreshToken;

import java.util.Optional;

public interface RefreshTokenService {
    public RefreshToken createRefreshToken(Long userId);
    public Optional<RefreshToken> findByToken(String token);
    public RefreshToken verifyExpiration(RefreshToken refreshToken);
    public int deleteByUserId(Long userId);
}
