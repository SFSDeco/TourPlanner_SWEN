package com.FHTW.tourplanner_swen.service;

import com.FHTW.tourplanner_swen.service.dtos.TourLogDto;

import java.util.List;

public interface TourLogService {
    void addNewTourLog(TourLogDto tourLogDto, Long tourId);
    List<TourLogDto> getAllLogs();

    List<TourLogDto> getTourLogs(Long tourId);

    void updateTourLog(Long tourId, TourLogDto tourLogDto);

    void deleteTourLog(Long logId);
}
