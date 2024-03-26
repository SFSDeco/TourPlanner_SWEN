package com.example.tourplanner_swen.service;

import com.example.tourplanner_swen.service.dtos.TourLogDto;

import java.util.List;

public interface TourLogService {
    void addNewTourLog(TourLogDto tourLogDto, Long tourId);
    List<TourLogDto> getAllLogs();
}
