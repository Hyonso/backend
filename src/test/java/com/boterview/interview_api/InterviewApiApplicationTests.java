package com.boterview.interview_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.boterview.interview_api.config.TestSecurityConfig;
import com.boterview.interview_api.security.authentication.jwt.registry.TestJwtRegistryConfig;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import({ TestJwtRegistryConfig.class, TestSecurityConfig.class })
class InterviewApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
