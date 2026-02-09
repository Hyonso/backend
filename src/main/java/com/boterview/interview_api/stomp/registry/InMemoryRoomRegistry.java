package com.boterview.interview_api.stomp.registry;


import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.boterview.interview_api.stomp.model.Room;

@Component
public class InMemoryRoomRegistry implements RoomRegistry {

	private final ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, String> sessionToRoom = new ConcurrentHashMap<>();

	@Override
	public Room getOrCreateRoom(String roomId) {
		return rooms.computeIfAbsent(roomId, id -> new Room(id));
	}

	@Override
	public Room getRoom(String roomId) {
		return rooms.get(roomId);
	}

	@Override
	public void removeRoom(String roomId) {
		rooms.remove(roomId);
	}

	@Override
	public void registerSession(String sessionId, String roomId) {
		sessionToRoom.put(sessionId, roomId);
	}

	@Override
	public String getRoomIdBySessionId(String sessionId) {
		return sessionToRoom.get(sessionId);
	}

	@Override
	public void unregisterSession(String sessionId) {
		sessionToRoom.remove(sessionId);
	}
}
