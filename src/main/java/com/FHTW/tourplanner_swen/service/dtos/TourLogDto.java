package com.FHTW.tourplanner_swen.service.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TourLogDto {
    private Long id;
    private String comment;
    private TourDto tour;
    private String difficulty;
    private int rating;
    private LocalDate log_date;
}
