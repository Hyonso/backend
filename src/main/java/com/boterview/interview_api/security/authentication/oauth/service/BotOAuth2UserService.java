package com.boterview.interview_api.security.authentication.oauth.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.boterview.interview_api.domain.user.entity.OAuthProvider;
import com.boterview.interview_api.domain.user.entity.User;
import com.boterview.interview_api.domain.user.repository.UserMapper;
import com.boterview.interview_api.security.authentication.oauth.OAuth2UserInfoFactory;
import com.boterview.interview_api.security.authentication.oauth.dto.OAuth2UserInfo;
import com.boterview.interview_api.security.core.dto.SecurityUserDto;
import com.boterview.interview_api.security.core.principal.BotUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class BotOAuth2UserService extends DefaultOAuth2UserService {

    private final UserMapper userMapper;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        return processOAuth2User(userRequest, oauth2User);
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuthProvider provider = OAuthProvider.valueOf(registrationId.toUpperCase());

        Map<String, Object> attributes = oauth2User.getAttributes();
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, attributes);

        User user = userMapper.findByEmail(oAuth2UserInfo.getEmail())
                .map(existingUser -> updateExistingUser(existingUser, oAuth2UserInfo))
                .orElseGet(() -> registerNewUser(provider, oAuth2UserInfo));

        SecurityUserDto userDto = SecurityUserDto.from(user);
        return new BotUserDetails(userDto, null);
    }

    private User registerNewUser(OAuthProvider provider, OAuth2UserInfo oAuth2UserInfo) {
        User user = User.builder()
                .userId(UUID.randomUUID().toString())
                .email(oAuth2UserInfo.getEmail())
                .name(oAuth2UserInfo.getName())
                .oauth(provider)
                .createdAt(LocalDateTime.now())
                .build();
        userMapper.insert(user);
        return user;
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.updateName(oAuth2UserInfo.getName());
        userMapper.update(existingUser);
        return existingUser;
    }
}
