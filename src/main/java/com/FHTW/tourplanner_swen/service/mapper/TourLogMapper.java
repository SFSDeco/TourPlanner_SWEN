package com.FHTW.tourplanner_swen.service.mapper;

import com.FHTW.tourplanner_swen.service.dtos.TourLogDto;
import com.FHTW.tourplanner_swen.persistence.entities.TourLogEntity;
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
