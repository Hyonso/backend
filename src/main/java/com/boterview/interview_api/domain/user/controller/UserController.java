package com.boterview.interview_api.domain.user.controller;

import com.boterview.interview_api.domain.user.service.UserService;
import com.boterview.interview_api.domain.user.dto.UserResponseDto;
import com.boterview.interview_api.domain.user.dto.UserUpdateDto;
import com.boterview.interview_api.security.core.principal.BotUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserResponseDto> getUser(@AuthenticationPrincipal BotUserDetails userDetails) {
        UserResponseDto userResponse = userService.getUser(userDetails.getUserDto().getUserId());
        return ResponseEntity.ok(userResponse);
    }

    @PutMapping
    public ResponseEntity<Map<String, Boolean>> updateUser(@AuthenticationPrincipal BotUserDetails userDetails,
            @RequestBody UserUpdateDto userUpdateDto) {
        userService.updateUser(userDetails.getUserDto().getUserId(), userUpdateDto);
        return ResponseEntity.ok(Map.of("updated", true));
    }

    @DeleteMapping
    public ResponseEntity<Map<String, Boolean>> deleteUser(@AuthenticationPrincipal BotUserDetails userDetails) {
        userService.deleteUser(userDetails.getUserDto().getUserId());
        return ResponseEntity.ok(Map.of("deleted", true));
    }
}
