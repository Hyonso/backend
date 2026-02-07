package com.boterview.interview_api.security.authentication.jwt.registry;

import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.boterview.interview_api.security.authentication.jwt.dto.JwtInformation;
import com.boterview.interview_api.security.authentication.jwt.provider.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Profile("local")
@Component
@RequiredArgsConstructor
public class InMemoryJwtRegistry implements JwtRegistry {

    private final Map<String, Queue<JwtInformation>> session = new ConcurrentHashMap<>();

    private final Set<String> accessTokenIndexes = ConcurrentHashMap.newKeySet();
    private final Set<String> refreshTokenIndexes = ConcurrentHashMap.newKeySet();

    private final int maxActiveJwtCount = 1;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void registerJwtInformation(JwtInformation jwtInformation) {
        session.compute(jwtInformation.getUserDto().getUserId(), (key, queue) -> {
            if (queue == null) {
                queue = new ConcurrentLinkedQueue<>();
            }
            if (queue.size() >= maxActiveJwtCount) {
                JwtInformation deprecated = queue.poll();
                if (deprecated != null) {
                    removeTokenIndex(deprecated.getAccessToken(), deprecated.getRefreshToken());
                }
            }
            queue.add(jwtInformation);
            addTokenIndex(jwtInformation.getAccessToken(), jwtInformation.getRefreshToken());
            return queue;
        });
    }

    @Override
    public void invalidateJwtInformationByUserId(String userId) {
        session.computeIfPresent(userId, (key, queue) -> {
            queue.forEach(info -> removeTokenIndex(info.getAccessToken(), info.getRefreshToken()));
            queue.clear();
            return null;
        });
    }

    @Override
    public boolean hasActiveJwtInformationByUserId(String userId) {
        return session.containsKey(userId);
    }

    @Override
    public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
        return accessToken != null && accessTokenIndexes.contains(accessToken);
    }

    @Override
    public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
        return refreshToken != null && refreshTokenIndexes.contains(refreshToken);
    }

    @Override
    public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
        session.computeIfPresent(newJwtInformation.getUserDto().getUserId(), (key, queue) -> {
            queue.stream()
                    .filter(info -> info.getRefreshToken().equals(refreshToken))
                    .findFirst()
                    .ifPresent(info -> {
                        removeTokenIndex(info.getAccessToken(), info.getRefreshToken());
                        info.rotate(newJwtInformation.getAccessToken(), newJwtInformation.getRefreshToken());
                        addTokenIndex(newJwtInformation.getAccessToken(), newJwtInformation.getRefreshToken());
                    });
            return queue;
        });
    }

    @Scheduled(fixedDelay = 1000 * 60 * 5)
    @Override
    public void clearExpiredJwtInformation() {
        session.entrySet().removeIf(entry -> {
            Queue<JwtInformation> queue = entry.getValue();
            queue.removeIf(info -> {
                boolean isExpired = !jwtTokenProvider.validateAccessToken(info.getAccessToken())
                        || !jwtTokenProvider.validateRefreshToken(info.getRefreshToken());
                if (isExpired) {
                    removeTokenIndex(info.getAccessToken(), info.getRefreshToken());
                }
                return isExpired;
            });
            return queue.isEmpty();
        });
    }

    private void addTokenIndex(String accessToken, String refreshToken) {
        if (accessToken != null && !accessToken.isBlank()) accessTokenIndexes.add(accessToken);
        if (refreshToken != null && !refreshToken.isBlank()) refreshTokenIndexes.add(refreshToken);
    }

    private void removeTokenIndex(String accessToken, String refreshToken) {
        if (accessToken != null && !accessToken.isBlank()) accessTokenIndexes.remove(accessToken);
        if (refreshToken != null && !refreshToken.isBlank()) refreshTokenIndexes.remove(refreshToken);
    }
}
