package com.boterview.interview_api.stomp.dto;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor
@Builder
public class Envelope<T> {
	private String type;
	private String roomId;
	private String traceId;
	private T payload;
}
