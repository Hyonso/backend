package com.boterview.interview_api.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {
    private String name;

    // 비밀번호 변경 (선택사항)
    private String password; // 새 비밀번호 (프론트에서 password로 보냄)
}
