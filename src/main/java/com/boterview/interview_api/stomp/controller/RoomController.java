package com.boterview.interview_api.stomp.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.boterview.interview_api.stomp.dto.RoomResponse;
import com.boterview.interview_api.stomp.registry.RoomRegistry;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class RoomController {

	private final RoomRegistry roomRegistry;

	@PostMapping("/api/rooms")
	public RoomResponse createRoom(@RequestParam("setting_id") String settingId) {
		roomRegistry.getOrCreateRoom(settingId);
		return RoomResponse.builder().roomId(settingId).build();
	}

}
