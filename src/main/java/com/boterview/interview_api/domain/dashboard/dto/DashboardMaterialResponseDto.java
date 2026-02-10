package com.boterview.interview_api.domain.dashboard.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardMaterialResponseDto {

    private String interviewId;
    private List<MaterialDto> items;
    private CountsDto counts;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MaterialDto {
        private String materialId;
        private String type;
        private String s3Uri;
        private LocalDateTime createdAt;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CountsDto {
        private int materialCount;
    }
}
