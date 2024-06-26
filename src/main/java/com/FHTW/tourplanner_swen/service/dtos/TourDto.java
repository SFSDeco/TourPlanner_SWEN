package com.FHTW.tourplanner_swen.service.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TourDto {
    private Long id;
    private String name;
    private String fromAddress;
    private String toAddress;
    private String transportation_type;
}
