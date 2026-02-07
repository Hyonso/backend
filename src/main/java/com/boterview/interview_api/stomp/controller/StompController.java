package com.boterview.interview_api.stomp.controller;



import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestClient;

import com.boterview.interview_api.stomp.dto.Envelope;
import com.boterview.interview_api.stomp.model.Room;
import com.boterview.interview_api.stomp.model.RoomRole;
import com.boterview.interview_api.stomp.model.SessionInfo;
import com.boterview.interview_api.stomp.registry.RoomRegistry;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class StompController {

	private final RoomRegistry roomRegistry;
	private final RestClient restClient = RestClient.create();
	private final SimpMessagingTemplate messagingTemplate;

	@Value("${ai.server.uri}")
	private String aiServerUri;

	@MessageMapping("/signal/join")
	public void handleJoin(Envelope envelope, SimpMessageHeaderAccessor headerAccessor){
		String sessionId = headerAccessor.getSessionId();
		String roomId = envelope.getRoomId();

		Room room = roomRegistry.getOrCreateRoom(roomId);
		roomRegistry.registerSession(sessionId, roomId);
		SessionInfo sessionInfo = room.addParticipant(sessionId);

		if(sessionInfo.getRole().equals(RoomRole.Caller)){
			restClient.post()
				.uri(aiServerUri+"/notify/")
				.contentType(MediaType.APPLICATION_JSON)
				.body(Map.of("roomId", roomId))
				.retrieve()
				.toBodilessEntity();
		}
	}

	@MessageMapping("/signal/webrtc/offer")
	public void handleWebRtcOffer(Envelope envelope){
		String roomId = envelope.getRoomId();
		messagingTemplate.convertAndSend("/topic/webrtc/offer/" + roomId, envelope);
	}

	@MessageMapping("/signal/webrtc/answer")
	public void handleWebRTCAnswer(Envelope envelope) {
		String roomId = envelope.getRoomId();
		messagingTemplate.convertAndSend("/topic/webrtc/answer/" + roomId, envelope);
	}

	@MessageMapping("/signal/webrtc/ice")
	public void handleICECandidate(Envelope envelope) {
		String roomId = envelope.getRoomId();
		messagingTemplate.convertAndSend("/topic/webrtc/ice/" + roomId, envelope);
	}

}
