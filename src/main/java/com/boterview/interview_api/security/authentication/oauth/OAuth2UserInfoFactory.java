package com.boterview.interview_api.security.authentication.oauth;

import java.util.Map;

import com.boterview.interview_api.security.authentication.oauth.dto.GoogleOAuth2UserInfo;
import com.boterview.interview_api.security.authentication.oauth.dto.KakaoOAuth2UserInfo;
import com.boterview.interview_api.security.authentication.oauth.dto.OAuth2UserInfo;
import com.boterview.interview_api.security.authentication.oauth.exception.UnSupportedOAuthException;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if ("google".equalsIgnoreCase(registrationId)) {
            return new GoogleOAuth2UserInfo(attributes);
        } else if ("kakao".equalsIgnoreCase(registrationId)) {
            return new KakaoOAuth2UserInfo(attributes);
        } else {
            throw new UnSupportedOAuthException(registrationId);
        }
    }
}
