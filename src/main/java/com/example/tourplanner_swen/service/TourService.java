package com.example.tourplanner_swen.service;

import com.example.tourplanner_swen.service.dtos.TourDto;

import java.util.List;

public interface TourService {
    void saveNewTour(TourDto tourDto);

    List<TourDto> getAllTours();
    List<TourDto> getTourByName(String name);
}
