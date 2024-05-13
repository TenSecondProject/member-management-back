package org.colcum.admin.global.common.application;

import lombok.RequiredArgsConstructor;
import org.colcum.admin.domain.user.domain.UserEntity;
import org.colcum.admin.global.auth.api.dto.RefreshToken;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> refreshTokenRedisTemplate;
    private static final String REDIS_USER_PREFIX = "user:";

    @Transactional
    public RefreshToken createRefreshToken() {
        LocalDateTime expiryTime = LocalDateTime.now().plusDays(1);
        String randomUUID = UUID.randomUUID().toString();
        return new RefreshToken(randomUUID, expiryTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    @Transactional
    public void saveRefreshToken(RefreshToken refreshToken, UserEntity user) {
        refreshTokenRedisTemplate.opsForHash().put("user:" + refreshToken.getUuid(), "userId", user.getId());
        refreshTokenRedisTemplate.opsForHash().put("user:" + refreshToken.getUuid(), "role", user.getUserType().getRole());
        refreshTokenRedisTemplate.opsForHash().put("user:" + refreshToken.getUuid(), "expiryDate", refreshToken.getExpiryDate());
        refreshTokenRedisTemplate.expire(refreshToken.getUuid(), 1L, TimeUnit.DAYS);
    }

    public boolean isRefreshTokenExpired(String refreshTokenKey) {
        Object expireDateObj = refreshTokenRedisTemplate.opsForHash().get(REDIS_USER_PREFIX + refreshTokenKey, "expiryDate");
        if (expireDateObj == null) return true;

        LocalDateTime expireDate = LocalDateTime.parse(
            (String) expireDateObj,
                DateTimeFormatter.ISO_LOCAL_DATE_TIME
        );
        return expireDate.isBefore(LocalDateTime.now());
    }

    public Long getUserIdInRefreshToken(String refreshTokenKey) {
        Object userId = refreshTokenRedisTemplate.opsForHash().get(REDIS_USER_PREFIX + refreshTokenKey, "userId");
        if (userId == null) {
            throw new UsernameNotFoundException("해당 유저는 존재하지 않습니다.");
        }
        return Long.valueOf(userId.toString());
    }

    public String getUserRoleInRefreshToken(String refreshTokenKey) {
        Object role = refreshTokenRedisTemplate.opsForHash().get(REDIS_USER_PREFIX + refreshTokenKey, "role");
        if (role == null) {
            throw new UsernameNotFoundException("해당 유저는 존재하지 않습니다.");
        }
        return role.toString();
    }

    @Transactional
    public RefreshToken renewRefreshToken(String oldKey) {
        Boolean hasKey = refreshTokenRedisTemplate.hasKey(REDIS_USER_PREFIX + oldKey);
        if (hasKey == null || !hasKey) {
            throw new IllegalStateException("해당 refreshKey는 존재하지 않습니다.");
        }
        String newKey = UUID.randomUUID().toString();
        refreshTokenRedisTemplate.rename(REDIS_USER_PREFIX + oldKey, REDIS_USER_PREFIX + newKey);
        return new RefreshToken(newKey, LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

}
