package com.example.tourplanner_swen.service.mapper;

import com.example.tourplanner_swen.persistence.entities.TourEntity;
import com.example.tourplanner_swen.service.dtos.TourDto;

public class TourMapper extends AbstractMapper<TourEntity, TourDto>{
    @Override
    public TourDto mapToDto(TourEntity source){
        return TourDto.builder()
                .id(source.getId())
                .name(source.getName())
                .build();
    }
}
