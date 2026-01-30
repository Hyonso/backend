package com.boterview.interview_api.security.authentication.oauth.dto;

public interface OAuth2UserInfo {
	String getProviderId();

	String getEmail();

	String getName();

	String getPicture();
}
