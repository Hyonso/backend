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

        boolean nameChanged = false;
        boolean passwordChanged = false;

        // 이름 변경
        if (dto.getName() != null && !dto.getName().isBlank()) {
            user.updateName(dto.getName());
            nameChanged = true;
        }

        // 비밀번호 변경 (OAuth 사용자는 변경 불가)
        if (dto.getCurrentPassword() != null && dto.getNewPassword() != null) {
            // OAuth 사용자 체크
            if (user.getOauth() != null) {
                throw new BaseException(ErrorCode.AUTH_PASSWORD_ERROR);
            }

            // 현재 비밀번호 확인
            if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
                throw new BaseException(ErrorCode.AUTH_CURRENT_PASSWORD_MISMATCH);
            }

            // 새 비밀번호로 변경
            String encodedNewPassword = passwordEncoder.encode(dto.getNewPassword());
            userMapper.updatePassword(userId, encodedNewPassword);
            passwordChanged = true;
        }

        // 이름만 변경된 경우에만 update 호출 (비밀번호는 이미 updatePassword로 처리됨)
        if (nameChanged && !passwordChanged) {
            userMapper.update(user);
        } else if (nameChanged && passwordChanged) {
            // 둘 다 변경된 경우, 이름만 update (비밀번호는 이미 업데이트됨)
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
