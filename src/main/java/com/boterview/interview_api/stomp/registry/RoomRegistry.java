package com.boterview.interview_api.stomp.registry;



import com.boterview.interview_api.stomp.model.Room;

public interface RoomRegistry {
	Room getOrCreateRoom(String roomId);

	Room getRoom(String roomId);

	void removeRoom(String roomId);

	void registerSession(String sessionId, String roomId);

	String getRoomIdBySessionId(String sessionId);

	void unregisterSession(String sessionId);
}
