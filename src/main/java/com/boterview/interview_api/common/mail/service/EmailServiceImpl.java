package com.boterview.interview_api.common.mail.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.properties.mail.from}")
    private String fromEmail;

    @Override
    public void sendTemporaryPassword(String email, String temporaryPassword) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject("[Boterview] 임시 비밀번호 안내");
            helper.setText(buildEmailContent(temporaryPassword), true);

            mailSender.send(message);
            log.info("임시 비밀번호 이메일 전송 성공. email={}", email);
        } catch (MessagingException e) {
            log.error("임시 비밀번호 이메일 전송 실패. email={}", email, e);
            throw new RuntimeException("이메일 발송에 실패했습니다.", e);
        }
    }

    private String buildEmailContent(String temporaryPassword) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }
                        .content { background-color: #f9f9f9; padding: 30px; border-radius: 5px; margin-top: 20px; }
                        .password-box { background-color: #fff; border: 2px solid #4CAF50; padding: 15px; text-align: center; font-size: 24px; font-weight: bold; color: #4CAF50; margin: 20px 0; border-radius: 5px; }
                        .warning { color: #d32f2f; font-weight: bold; margin-top: 20px; }
                        .footer { text-align: center; margin-top: 30px; color: #666; font-size: 12px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Boterview 임시 비밀번호 안내</h1>
                        </div>
                        <div class="content">
                            <p>안녕하세요,</p>
                            <p>비밀번호 재설정 요청에 따라 <strong>임시 비밀번호</strong>를 발급해 드립니다.</p>

                            <div class="password-box">
                                %s
                            </div>

                            <p>위 임시 비밀번호로 로그인하신 후, <strong>반드시 새로운 비밀번호로 변경</strong>해 주시기 바랍니다.</p>

                            <div class="warning">
                                ⚠️ 보안을 위해 로그인 후 즉시 비밀번호를 변경해주세요.
                            </div>

                            <p style="margin-top: 20px;">본인이 요청하지 않은 경우, 즉시 고객센터로 문의해주시기 바랍니다.</p>
                        </div>
                        <div class="footer">
                            <p>본 메일은 발신 전용입니다. 문의사항은 고객센터를 이용해주세요.</p>
                            <p>&copy; 2026 Boterview. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
                """
                .formatted(temporaryPassword);
    }
}
