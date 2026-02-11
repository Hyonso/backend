package com.boterview.interview_api.common.mail.listener;

import com.boterview.interview_api.common.mail.service.EmailService;
import com.boterview.interview_api.security.event.PasswordResetEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailEventListener {

    private final EmailService emailService;

    @Async
    @EventListener
    public void handlePasswordResetEvent(PasswordResetEvent event) {
        try {
            emailService.sendTemporaryPassword(event.email(), event.temporaryPassword());
            log.info("임시 비밀번호 이메일 발송 완료. email={}", event.email());
        } catch (Exception e) {
            log.error("임시 비밀번호 이메일 발송 실패. email={}, error={}", event.email(), e.getMessage(), e);
        }
    }
}
