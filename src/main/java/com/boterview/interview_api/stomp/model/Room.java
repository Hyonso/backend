package com.boterview.interview_api.stomp.model;


import java.util.concurrent.ConcurrentHashMap;

import lombok.Data;

@Data
public class Room {

	private final String roomId;
	private final ConcurrentHashMap<String, SessionInfo> sessionInfoMap = new ConcurrentHashMap<>();

	public Room(String roomId){
		this.roomId = roomId;
	}

	public SessionInfo addParticipant(String sessionId){
		RoomRole role = sessionInfoMap.isEmpty() ? RoomRole.Caller : RoomRole.Callee;
		SessionInfo info = new SessionInfo(role, sessionId);
		sessionInfoMap.put(sessionId, info);
		return info;
	}

	public void removeParticipant(String sessionId){
		sessionInfoMap.remove(sessionId);
	}

	public boolean isEmpty(){
		return sessionInfoMap.isEmpty();
	}
}
