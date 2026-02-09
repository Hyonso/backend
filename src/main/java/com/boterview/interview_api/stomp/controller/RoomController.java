package com.boterview.interview_api.stomp.controller;

import java.util.Map;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.boterview.interview_api.stomp.dto.RoomResponse;
import com.boterview.interview_api.stomp.registry.RoomRegistry;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class RoomController {

	private final RoomRegistry roomRegistry;

	@PostMapping("/api/rooms")
	public RoomResponse createRoom(){
		        String roomId = java.util.UUID.randomUUID().toString();
		        roomRegistry.getOrCreateRoom(roomId);
		        return RoomResponse.builder().roomId(roomId).build();	}

}
