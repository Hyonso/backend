package com.boterview.interview_api.security.authentication.jwt.registry;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.boterview.interview_api.security.authentication.jwt.dto.JwtInformation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile({"prod", "dev"})
@Component
@RequiredArgsConstructor
public class RedisJwtRegistry implements JwtRegistry {

    private static final String ACCESS_PREFIX = "jwt:access:";
    private static final String REFRESH_PREFIX = "jwt:refresh:";
    private static final String USER_TOKENS_PREFIX = "jwt:user:tokens:";
    private static final String USER_ACCESS_TOKENS_PREFIX = "jwt:user:access:";

    private final RedisTemplate<String, String> stringRedisTemplate;

    @Value("${jwt.access-token.expiration-ms}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration-ms}")
    private long refreshTokenExpiration;

    @Value("${jwt.max-active}")
    private int maxActiveCount;

    @Override
    public void registerJwtInformation(JwtInformation jwtInformation) {
        String userId = jwtInformation.getUserDto().getUserId();
        String accessToken = jwtInformation.getAccessToken();
        String refreshToken = jwtInformation.getRefreshToken();

        stringRedisTemplate.opsForValue()
                .set(ACCESS_PREFIX + accessToken, userId, accessTokenExpiration, TimeUnit.MILLISECONDS);
        stringRedisTemplate.opsForValue()
                .set(REFRESH_PREFIX + refreshToken, userId, refreshTokenExpiration, TimeUnit.MILLISECONDS);

        String userTokensKey = USER_TOKENS_PREFIX + userId;
        String userAccessTokenKey = USER_ACCESS_TOKENS_PREFIX + userId;

        Long tokenCount = stringRedisTemplate.opsForList().size(userTokensKey);

        if (tokenCount != null && tokenCount >= maxActiveCount) {
            String oldRefreshToken = stringRedisTemplate.opsForList().leftPop(userTokensKey);
            String oldAccessToken = stringRedisTemplate.opsForList().leftPop(userAccessTokenKey);

            if (oldRefreshToken != null) stringRedisTemplate.delete(REFRESH_PREFIX + oldRefreshToken);
            if (oldAccessToken != null) stringRedisTemplate.delete(ACCESS_PREFIX + oldAccessToken);
        }

        stringRedisTemplate.opsForList().rightPush(userTokensKey, refreshToken);
        stringRedisTemplate.opsForList().rightPush(userAccessTokenKey, accessToken);

        stringRedisTemplate.expire(userTokensKey, refreshTokenExpiration, TimeUnit.MILLISECONDS);
        stringRedisTemplate.expire(userAccessTokenKey, accessTokenExpiration, TimeUnit.MILLISECONDS);
    }

    @Override
    public void invalidateJwtInformationByUserId(String userId) {
        String userTokensKey = USER_TOKENS_PREFIX + userId;
        String userAccessTokensKey = USER_ACCESS_TOKENS_PREFIX + userId;

        List<String> userRefreshTokens = stringRedisTemplate.opsForList().range(userTokensKey, 0, -1);
        if (userRefreshTokens != null) {
            userRefreshTokens.forEach(token -> stringRedisTemplate.delete(REFRESH_PREFIX + token));
        }
        List<String> userAccessTokens = stringRedisTemplate.opsForList().range(userAccessTokensKey, 0, -1);
        if (userAccessTokens != null) {
            userAccessTokens.forEach(token -> stringRedisTemplate.delete(ACCESS_PREFIX + token));
        }

        stringRedisTemplate.delete(userTokensKey);
        stringRedisTemplate.delete(userAccessTokensKey);
    }

    @Override
    public boolean hasActiveJwtInformationByUserId(String userId) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(USER_TOKENS_PREFIX + userId));
    }

    @Override
    public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(ACCESS_PREFIX + accessToken));
    }

    @Override
    public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(REFRESH_PREFIX + refreshToken));
    }

    @Override
    public void rotateJwtInformation(String oldRefreshToken, JwtInformation newJwtInformation) {
        String userId = newJwtInformation.getUserDto().getUserId();
        String userTokensKey = USER_TOKENS_PREFIX + userId;
        String userAccessTokensKey = USER_ACCESS_TOKENS_PREFIX + userId;

        stringRedisTemplate.delete(REFRESH_PREFIX + oldRefreshToken);

        String newAccessToken = newJwtInformation.getAccessToken();
        String newRefreshToken = newJwtInformation.getRefreshToken();

        stringRedisTemplate.opsForValue()
                .set(ACCESS_PREFIX + newAccessToken, userId, accessTokenExpiration, TimeUnit.MILLISECONDS);
        stringRedisTemplate.opsForValue()
                .set(REFRESH_PREFIX + newRefreshToken, userId, refreshTokenExpiration, TimeUnit.MILLISECONDS);

        List<String> refreshTokens = stringRedisTemplate.opsForList().range(userTokensKey, 0, -1);
        if (refreshTokens != null) {
            for (int i = 0; i < refreshTokens.size(); i++) {
                if (refreshTokens.get(i).equals(oldRefreshToken)) {
                    stringRedisTemplate.opsForList().set(userTokensKey, i, newRefreshToken);
                    stringRedisTemplate.opsForList().set(userAccessTokensKey, i, newAccessToken);
                    break;
                }
            }
        }

        stringRedisTemplate.expire(userTokensKey, refreshTokenExpiration, TimeUnit.MILLISECONDS);
        stringRedisTemplate.expire(userAccessTokensKey, accessTokenExpiration, TimeUnit.MILLISECONDS);
    }

    @Override
    public void clearExpiredJwtInformation() {
        // Redis TTL handles expiration automatically
    }
}
