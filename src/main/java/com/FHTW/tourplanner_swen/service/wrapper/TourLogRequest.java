package com.FHTW.tourplanner_swen.service.wrapper;


import com.FHTW.tourplanner_swen.service.dtos.TourLogDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TourLogRequest {
    private TourLogDto tourLog;
    private Long tourId;
}
