package com.FHTW.tourplanner_swen.service.impl;

import com.FHTW.tourplanner_swen.service.mapper.TourMapper;
import com.FHTW.tourplanner_swen.persistence.entities.TourEntity;
import com.FHTW.tourplanner_swen.persistence.repositories.TourRepository;
import com.FHTW.tourplanner_swen.service.TourService;
import com.FHTW.tourplanner_swen.service.dtos.TourDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class TourServiceImpl implements TourService {

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private TourMapper tourMapper;

    @Override
    public void saveNewTour(TourDto tourDto){
        TourEntity entity = TourEntity.builder()
                .id(tourDto.getId())
                .name(tourDto.getName())
                .build();
        tourRepository.save(entity);
    }

    @Override
    public List<TourDto> getAllTours() {return tourMapper.mapToDto(tourRepository.findAll());}

    @Override
    public List<TourDto> getTourByName(String name){
        return tourMapper.mapToDto(tourRepository.findByNameIgnoreCase(name));
    }

}
