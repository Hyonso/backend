package com.boterview.interview_api.stomp.registry;



import com.boterview.interview_api.stomp.model.Room;

public interface RoomRegistry {
	Room getOrCreateRoom(String roomId);

	Room getRoom(String roomId);
}
