package com.example.tourplanner_swen.service.mapper;

import com.example.tourplanner_swen.persistence.entities.TourLogEntity;
import com.example.tourplanner_swen.service.dtos.TourLogDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TourLogMapper extends AbstractMapper<TourLogEntity, TourLogDto> {

    @Autowired
    private TourMapper tourMapper;

    @Override
    public TourLogDto mapToDto(TourLogEntity source){

        return TourLogDto.builder()
                .id(source.getId())
                .comment(source.getComment())
                .tour(tourMapper.mapToDto(source.getTour()))
                .build();
    }
}
