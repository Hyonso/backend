package com.boterview.interview_api.common.mail.service;

public interface EmailService {
    void sendTemporaryPassword(String email, String temporaryPassword);
}
