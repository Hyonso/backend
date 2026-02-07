package com.boterview.interview_api.stomp.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.boterview.interview_api.stomp.model.Room;
import com.boterview.interview_api.stomp.registry.RoomRegistry;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

	private final RoomRegistry roomRegistry;

	@EventListener(SessionDisconnectEvent.class)
	public void handleSessionDisconnect(SessionDisconnectEvent event) {
		String sessionId = event.getSessionId();
		String roomId = roomRegistry.getRoomIdBySessionId(sessionId);

		if (roomId != null) {
			Room room = roomRegistry.getRoom(roomId);
			if (room != null) {
				room.removeParticipant(sessionId);
				if (room.isEmpty()) {
					roomRegistry.removeRoom(roomId);
				}
			}
			roomRegistry.unregisterSession(sessionId);
		}
	}
}
