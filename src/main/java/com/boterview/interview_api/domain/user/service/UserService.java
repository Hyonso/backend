package com.boterview.interview_api.domain.user.service;

import com.boterview.interview_api.common.exception.BaseException;
import com.boterview.interview_api.common.exception.ErrorCode;
import com.boterview.interview_api.domain.user.dto.UserResponseDto;
import com.boterview.interview_api.domain.user.dto.UserUpdateDto;
import com.boterview.interview_api.domain.user.entity.User;
import com.boterview.interview_api.domain.user.repository.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDto getUser(String userId) {
        User user = userMapper.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
        return UserResponseDto.from(user);
    }

    public UserResponseDto updateUser(String userId, UserUpdateDto dto) {
        User user = userMapper.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        boolean isChanged = false;

        // 이름 변경
        if (dto.getName() != null && !dto.getName().isBlank()) {
            user.updateName(dto.getName());
            isChanged = true;
        }

        // 비밀번호 변경 (OAuth 사용자는 변경 불가)
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            // OAuth 사용자 체크
            if (user.getOauth() != null) {
                throw new BaseException(ErrorCode.AUTH_PASSWORD_ERROR);
            }

            // 새 비밀번호로 변경 (객체 상태 업데이트)
            String encodedNewPassword = passwordEncoder.encode(dto.getPassword());
            user.updatePassword(encodedNewPassword);
            isChanged = true;
        }

        // 변경사항이 있으면 DB 업데이트
        if (isChanged) {
            userMapper.update(user);
        }

        return UserResponseDto.from(user);
    }

    public void deleteUser(String userId) {
        if (userMapper.findById(userId).isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        userMapper.delete(userId);
    }
}
