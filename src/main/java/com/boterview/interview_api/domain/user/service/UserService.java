package com.boterview.interview_api.domain.user.service;

import com.boterview.interview_api.domain.user.dto.UserResponseDto;
import com.boterview.interview_api.domain.user.dto.UserUpdateDto;
import com.boterview.interview_api.domain.user.entity.User;
import com.boterview.interview_api.domain.user.repository.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    public UserResponseDto getUser(String userId) {
        User user = userMapper.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return UserResponseDto.from(user);
    }

    public UserResponseDto updateUser(String userId, UserUpdateDto dto) {
        User user = userMapper.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (dto.getName() != null) {
            user.updateName(dto.getName());
        }

        userMapper.update(user);
        return UserResponseDto.from(user);
    }

    public void deleteUser(String userId) {
        if (userMapper.findById(userId).isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        userMapper.delete(userId);
    }
}
