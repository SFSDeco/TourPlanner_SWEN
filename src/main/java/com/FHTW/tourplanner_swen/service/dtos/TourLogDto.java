package com.FHTW.tourplanner_swen.service.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TourLogDto {
    private Long id;
    private String comment;
    private TourDto tour;
}
