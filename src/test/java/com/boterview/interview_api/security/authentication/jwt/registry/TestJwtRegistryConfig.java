package com.boterview.interview_api.security.authentication.jwt.registry;

import com.boterview.interview_api.security.authentication.jwt.dto.JwtInformation;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 테스트용 JwtRegistry - Redis 대신 메모리 사용
 */
@TestConfiguration
public class TestJwtRegistryConfig {

    @Bean
    @Primary
    public JwtRegistry testJwtRegistry() {
        return new InMemoryJwtRegistry();
    }

    /**
     * 메모리 기반 JWT Registry 구현
     */
    public static class InMemoryJwtRegistry implements JwtRegistry {
        // userId -> JwtInformation
        private final Map<String, JwtInformation> userStore = new ConcurrentHashMap<>();
        // accessToken -> userId
        private final Map<String, String> accessTokenStore = new ConcurrentHashMap<>();
        // refreshToken -> userId
        private final Map<String, String> refreshTokenStore = new ConcurrentHashMap<>();

        @Override
        public void registerJwtInformation(JwtInformation jwtInformation) {
            String userId = jwtInformation.getUserDto().getUserId();
            userStore.put(userId, jwtInformation);
            accessTokenStore.put(jwtInformation.getAccessToken(), userId);
            refreshTokenStore.put(jwtInformation.getRefreshToken(), userId);
        }

        @Override
        public void invalidateJwtInformationByUserId(String userId) {
            JwtInformation jwt = userStore.remove(userId);
            if (jwt != null) {
                accessTokenStore.remove(jwt.getAccessToken());
                refreshTokenStore.remove(jwt.getRefreshToken());
            }
        }

        @Override
        public boolean hasActiveJwtInformationByUserId(String userId) {
            return userStore.containsKey(userId);
        }

        @Override
        public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
            return accessTokenStore.containsKey(accessToken);
        }

        @Override
        public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
            return refreshTokenStore.containsKey(refreshToken);
        }

        @Override
        public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
            String userId = refreshTokenStore.get(refreshToken);
            if (userId != null) {
                invalidateJwtInformationByUserId(userId);
                registerJwtInformation(newJwtInformation);
            }
        }

        @Override
        public void clearExpiredJwtInformation() {
            // 테스트 환경에서는 만료 처리 생략
        }
    }
}
