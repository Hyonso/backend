package com.boterview.interview_api.security.core.principal;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.boterview.interview_api.domain.user.entity.User;
import com.boterview.interview_api.domain.user.repository.UserMapper;
import com.boterview.interview_api.security.core.dto.SecurityUserDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BotUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userMapper.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));

        SecurityUserDto dto = SecurityUserDto.from(user);
        return new BotUserDetails(dto, user.getPassword());
    }
}
