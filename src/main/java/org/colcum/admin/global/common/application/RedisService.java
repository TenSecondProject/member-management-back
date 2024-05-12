package org.colcum.admin.global.common.application;

import lombok.RequiredArgsConstructor;
import org.colcum.admin.domain.user.domain.UserEntity;
import org.colcum.admin.global.auth.api.dto.RefreshToken;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> refreshTokenRedisTemplate;

    public RefreshToken createRefreshToken() {
        LocalDateTime expiryTime = LocalDateTime.now().plusDays(1);
        String randomUUID = UUID.randomUUID().toString();
        return new RefreshToken(randomUUID, expiryTime);
    }

    public void saveRefreshToken(RefreshToken refreshToken, UserEntity user) {
        refreshTokenRedisTemplate.opsForHash().put("user:" + refreshToken.getUuid(), "userId", user.getId());
        refreshTokenRedisTemplate.opsForHash().put("user:" + refreshToken.getUuid(), "role", user.getUserType().getRole());
        refreshTokenRedisTemplate.opsForHash().put("user:" + refreshToken.getUuid(), "expiryDate", refreshToken.getExpiryDate());
        refreshTokenRedisTemplate.expire(refreshToken.getUuid(), 1L, TimeUnit.DAYS);
    }

    public boolean isRefreshTokenExpired(String refreshTokenKey) {
        LocalDateTime expireDate = (LocalDateTime) refreshTokenRedisTemplate.opsForHash().get("user:" + refreshTokenKey, "expiryDate");
        if (expireDate == null) return true;

        return expireDate.isBefore(LocalDateTime.now());
    }

    public Long getUserIdInRefreshToken(String refreshTokenKey) {
        return (Long) refreshTokenRedisTemplate.opsForHash().get("user:" + refreshTokenKey, "userId");
    }

    public String getUserRoleInRefreshToken(String refreshTokenKey) {
        return (String) refreshTokenRedisTemplate.opsForHash().get("user:" + refreshTokenKey, "role");
    }

}
