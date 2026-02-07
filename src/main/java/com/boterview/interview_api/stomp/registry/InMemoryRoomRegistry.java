package com.boterview.interview_api.stomp.registry;


import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.boterview.interview_api.stomp.model.Room;

@Component
@Profile("local")
public class InMemoryRoomRegistry implements RoomRegistry {

	private final ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>();

	@Override
	public Room getOrCreateRoom(String roomId) {
		return rooms.computeIfAbsent(roomId, id -> new Room(id));
	}

	@Override
	public Room getRoom(String roomId) {
		return rooms.get(roomId);
	}
}
