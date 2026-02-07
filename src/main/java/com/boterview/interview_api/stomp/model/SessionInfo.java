package com.boterview.interview_api.stomp.model;



import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SessionInfo {
	private final RoomRole role;
	private final String sessionId;
}
