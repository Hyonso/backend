package com.boterview.interview_api.security.authentication.jwt.registry;

import com.boterview.interview_api.security.authentication.jwt.dto.JwtInformation;

public interface JwtRegistry {

    void registerJwtInformation(JwtInformation jwtInformation);

    void invalidateJwtInformationByUserId(String userId);

    boolean hasActiveJwtInformationByUserId(String userId);

    boolean hasActiveJwtInformationByAccessToken(String accessToken);

    boolean hasActiveJwtInformationByRefreshToken(String refreshToken);

    void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation);

    void clearExpiredJwtInformation();
}
