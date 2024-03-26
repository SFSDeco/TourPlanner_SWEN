package com.example.tourplanner_swen.service.impl;

import com.example.tourplanner_swen.persistence.entities.TourEntity;
import com.example.tourplanner_swen.persistence.entities.TourLogEntity;
import com.example.tourplanner_swen.persistence.repositories.TourLogRepository;
import com.example.tourplanner_swen.persistence.repositories.TourRepository;
import com.example.tourplanner_swen.service.TourLogService;
import com.example.tourplanner_swen.service.dtos.TourLogDto;
import com.example.tourplanner_swen.service.mapper.TourLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TourLogServiceImpl implements TourLogService {
    @Autowired
    private TourLogRepository tourLogRepository;
    @Autowired
    private TourRepository tourRepository;
    @Autowired
    private TourLogMapper tourLogMapper;

    @Override
    public void addNewTourLog(TourLogDto tourLogDto, Long tourId){
        TourEntity tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new IllegalArgumentException("Tour not Found"));

        TourLogEntity tourLog = TourLogEntity.builder()
                .id(tourLogDto.getId())
                .comment(tourLogDto.getComment())
                .tour(tour)
                .build();

        tourLogRepository.save(tourLog);

    }

    @Override
    public List<TourLogDto> getAllLogs() {
        return tourLogMapper.mapToDto(tourLogRepository.findAll());
    }
}
