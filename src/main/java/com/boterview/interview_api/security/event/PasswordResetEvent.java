package com.boterview.interview_api.security.event;

import java.time.LocalDateTime;

public record PasswordResetEvent(
        String email,
        String temporaryPassword,
        LocalDateTime occurredAt) {
}
