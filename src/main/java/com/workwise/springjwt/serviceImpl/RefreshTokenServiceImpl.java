package com.workwise.springjwt.serviceImpl;

import com.workwise.springjwt.exception.TokenRefreshException;
import com.workwise.springjwt.models.RefreshToken;
import com.workwise.springjwt.repository.AccountRepository;
import com.workwise.springjwt.repository.RefreshTokenRepository;
import com.workwise.springjwt.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Value("${ww_be_v2.app.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private AccountRepository accountRepository;


    public RefreshToken createRefreshToken(Long userId) {
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(accountRepository.findById(userId).get());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }


    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken refreshToken) {
       if(refreshToken.getExpiryDate().compareTo(Instant.now()) < 0){
           refreshTokenRepository.delete(refreshToken);
           throw new TokenRefreshException(refreshToken.getToken(), "Refresh token was expired.");
       }
       return refreshToken;
    }


    public int deleteByUserId(Long userId) {
        return refreshTokenRepository.deleteByUser(accountRepository.findById(userId).get());
    }
}
